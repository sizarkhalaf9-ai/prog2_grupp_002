package se.su.inlupp;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

    private Stage stage;
    private BorderPane root;
    private Pane center;
    private Menu save;
    private VBox right;
    private ImageView image;
    private String imagePath;
    private Insets femPx = new Insets(5);
    private String font = "-fx-font: 12px 'Verdana';";

    private Button addNodeButton;
    private PutNodeHandler putNodeHandler;
    private String cityName;

    /*
     * Knappen som startar flödet för att lägga till en kant.
     */
    private Button addEdgeButton;

    private FileChooser fileChooser = new FileChooser();
    private boolean hasUnsavedChanges = false;

    private Button saveNewNode = new Button("Spara nod");

    /*
     * selectedCity är den stad som är markerad just nu.
     *
     * Den används av:
     * - Ta bort nod
     * - Lägg till kant
     * - Hitta väg
     */
    private City selectedCity;

    /*
     * Dessa används för "Lägg till kant".
     *
     * firstEdgeCity sparar första staden i kanten.
     * choosingSecondCityForEdge säger om nästa klick på en stad
     * ska tolkas som kantens andra stad.
     */
    private City firstEdgeCity;
    private boolean choosingSecondCityForEdge = false;

    /*
     * Dessa används för "Hitta väg".
     *
     * firstPathCity sparar startstaden.
     * choosingSecondCityForPath säger om nästa klick på en stad
     * ska tolkas som slutstaden.
     */
    private Button findPathButton;
    private City firstPathCity;
    private boolean choosingSecondCityForPath = false;

    /*
     * pathFinder är algoritmen som används för att hitta väg.
     *
     * Den är av typen PathFinder<City>, alltså ett interface.
     * Därför kan den bytas mellan BFS, DFS och Dijkstra under körning.
     *
     * BFS är standard från början.
     */
    private PathFinder<City> pathFinder = new BFSPathFinder<>();

    /*
     * Namnet på vald algoritm.
     * Används i dialogrutor så användaren ser vilken algoritm som används.
     */
    private String selectedAlgorithmName = "BFS";

    /*
     * Kopplar varje City från grafen till sin grafiska Destination på kartan.
     */
    private Map<City, Destination> cityDestinations = new HashMap<>();

    /*
     * Själva grafen från backend.
     *
     * Noderna är City.
     * Kanterna innehåller färdmedel och vikt.
     */
    private Graph<City> listGraph = new ListGraph<City>();

    /*
     * needle är den nod som användaren precis placerat ut,
     * men som ännu inte är sparad i grafen.
     */
    private Destination needle;

    private ObservableList<String> obsList = FXCollections.observableArrayList();


    @Override
    public void start(Stage stage) {
        this.stage = stage;
        root = new BorderPane();

        center = new Pane();
        center.setPickOnBounds(true);

        /*
         * Startbild innan användaren har valt karta.
         */
        image = new ImageView(new Image(App.class.getResourceAsStream("/empty.png")));

        /*
         * Kartbilden ska inte blockera klick.
         * Klick ska gå igenom bilden till center.
         */
        image.setMouseTransparent(true);
        center.getChildren().add(image);

        root.setCenter(center);

        Menu file = new Menu("Arkiv");

        MenuItem newMap = new MenuItem("Ny");
        newMap.setOnAction(new NewMapLoader());

        MenuItem open = new MenuItem("Öppna...");
        OpenHandler openHandler = new OpenHandler();
        open.setOnAction(openHandler);

        save = new Menu("Spara...");
        MenuItem saveGraph = new MenuItem("Projekt");
        MenuItem saveImage = new MenuItem("Bild");
        save.getItems().addAll(saveGraph, saveImage);
        save.setDisable(true);

        saveGraph.setOnAction(new SaveHandler());
        saveImage.setOnAction(new SaveImageHandler());

        MenuItem exit = new MenuItem("Avsluta");
        exit.setOnAction(new CloseWindowHandler());

        file.getItems().addAll(newMap, open, save, exit);

        MenuBar top = new MenuBar(file);
        root.setTop(top);

        right = new VBox(10);

        Label editLabel = new Label("Redigera graf");
        editLabel.setStyle("-fx-font: 18 'Verdana'; -fx-font-weight: bold;");

        addNodeButton = new Button("Lägg till stad");
        addNodeButton.setOnAction(new AddNodeHandler());

        saveNewNode.setDisable(true);

        /*
         * Ta bort nod-knappen.
         *
         * Den tar bort den markerade staden från både grafen och kartan.
         */
        Button removeNodeButton = new Button("Ta bort nod");
        removeNodeButton.setOnAction(new RemoveNodeHandler());

        /*
         * Lägg till kant-knappen.
         *
         * Flöde:
         * 1. Markera en stad.
         * 2. Tryck "Lägg till kant".
         * 3. Klicka på nästa stad.
         * 4. Fyll i färdmedel och vikt.
         */
        addEdgeButton = new Button("Lägg till kant");
        addEdgeButton.setOnAction(new StartAddEdgeHandler());

        /*
         * Hitta väg-knappen.
         *
         * Flöde:
         * 1. Markera startstad.
         * 2. Tryck "Hitta väg".
         * 3. Klicka på slutstad.
         * 4. Vald algoritm används.
         */
        findPathButton = new Button("Hitta väg");
        findPathButton.setOnAction(new StartFindPathHandler());

        List<Button> rightButtons = List.of(
                addNodeButton,
                saveNewNode,
                removeNodeButton,
                addEdgeButton,
                findPathButton);

        for (Button button : rightButtons) {
            button.setPrefWidth(110);
            button.setTextAlignment(TextAlignment.CENTER);
        }

        /*
         * Algoritmknappen visar BFS från början eftersom BFS är standard.
         */
        MenuButton algorithmButton = new MenuButton("BFS");
        algorithmButton.setPrefWidth(110);

        /*
         * DFS-valet.
         *
         * När användaren väljer DFS byts pathFinder-objektet.
         */
        MenuItem dfs = new MenuItem("Djupet först-sökning");
        dfs.setOnAction(event -> {
            pathFinder = new DFSPathFinder<>();
            selectedAlgorithmName = "DFS";
            algorithmButton.setText("DFS");
        });

        /*
         * BFS-valet.
         */
        MenuItem bfs = new MenuItem("Bredden först-sökning");
        bfs.setOnAction(event -> {
            pathFinder = new BFSPathFinder<>();
            selectedAlgorithmName = "BFS";
            algorithmButton.setText("BFS");
        });

        /*
         * Dijkstra-valet.
         */
        MenuItem dijkstra = new MenuItem("Dijkstras algoritm");
        dijkstra.setOnAction(event -> {
            pathFinder = new DijkstraPathFinder<>();
            selectedAlgorithmName = "Dijkstra";
            algorithmButton.setText("Dijkstra");
        });

        algorithmButton.getItems().addAll(dfs, bfs, dijkstra);

        right.getChildren().addAll(
                editLabel,
                addNodeButton,
                saveNewNode,
                removeNodeButton,
                addEdgeButton,
                findPathButton,
                algorithmButton);

        right.setAlignment(Pos.TOP_CENTER);
        right.setPadding(femPx);
        right.setStyle(font);

        Scene scene = new Scene(root);
        stage.setTitle("Reseplanerare");
        stage.setScene(scene);
        stage.setOnCloseRequest(new ExitHandler());
        stage.show();
    }

    public class NewMapLoader implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            if (hasUnsavedChanges) {
                Alert alert = new Alert(AlertType.CONFIRMATION,
                        "Det finns osparade ändringar. Vill du skapa en ny ändå?");
                alert.setHeaderText("Osparade ändringar");
                Optional<ButtonType> choice = alert.showAndWait();

                if (choice.isEmpty() || choice.get() == ButtonType.CANCEL) {
                    return;
                }
            }

            fileChooser.setInitialDirectory(new File("./src/main/resources/maps"));
            fileChooser.setTitle("Välj en karta du vill använda.");
            fileChooser.getExtensionFilters().add(
                    new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            File fileName = fileChooser.showOpenDialog(stage);
            fileChooser.getExtensionFilters().clear();

            if (fileName == null) return;   
            try {
                imagePath = fileName.toURI().toURL().toString();
                image = new ImageView(new Image(imagePath));
                //image = new ImageView(new Image(imagePath.substring(imagePath.indexOf("resources" + 9))));
            } catch (MalformedURLException e) {
            }
            center.getChildren().clear();
            listGraph = new ListGraph<>();
            cityDestinations.clear();
            selectedCity = null;
            firstEdgeCity = null;
            choosingSecondCityForEdge = false;
            needle = null;
            hasUnsavedChanges = false;
            save.setDisable(true);
            
            center.getChildren().add(image);
            root.setRight(right);
            stage.sizeToScene();
        } 
    }

    class CloseWindowHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class ExitHandler implements EventHandler<WindowEvent> {
        public void handle(WindowEvent event) {
            if (hasUnsavedChanges) {
                Alert alert = new Alert(
                        AlertType.CONFIRMATION,
                        "Det finns osparade ändringar. Vill du avsluta ändå?");
                alert.setHeaderText("Osparade ändringar");
                alert.setTitle("Varning");

                Optional<ButtonType> clicked = alert.showAndWait();

                if (clicked.isPresent() && clicked.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                }
            }
        }
    }

    /*
     * Sparar den nod som användaren har placerat ut på kartan.
     */
    class SaveNewNodeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            if (needle == null) {
                Alert alert = new Alert(AlertType.WARNING, "Klicka först på kartan där staden ska ligga.");
                alert.setHeaderText("Ingen plats vald");
                alert.showAndWait();
                return;
            }

            City city = new City(cityName, needle.getX(), needle.getY());

            if (listGraph.hasNode(city)) {
                Alert alert = new Alert(AlertType.WARNING, "Det finns redan en stad med detta namn.");
                alert.setHeaderText("Staden finns redan");
                alert.showAndWait();
                return;
            }

            /*
             * Kopplar klick- och flyttbeteende till noden.
             */
            attachDestinationHandlers(city, needle);

            /*
             * Lägger till staden i grafen.
             */
            listGraph.add(city);

            /*
             * Kopplar City-objektet till dess Destination på kartan.
             */
            cityDestinations.put(city, needle);

            needle = null;
            center.setOnMouseClicked(null);
            center.setCursor(Cursor.DEFAULT);
            addNodeButton.setDisable(false);

            hasUnsavedChanges = true;
            save.setDisable(false);
            saveNewNode.setDisable(true);

            System.out.println(listGraph.toString());
        }
    }

    /*
     * Körs när användaren klickar på kartan för att placera ut en stad.
     */
    class PutNodeHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            if (needle != null) {
                return;
            }

            needle = new Destination(cityName, event.getX(), event.getY());
            center.getChildren().add(needle);

            hasUnsavedChanges = true;
            save.setDisable(false);
            saveNewNode.setDisable(false);
            saveNewNode.setOnAction(new SaveNewNodeHandler());
        }
    }

    /*
     * Startar flödet för att lägga till stad.
     */
    class AddNodeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            /*
             * Om användaren börjar lägga till stad avbryts kantläge och vägläge.
             */
            choosingSecondCityForEdge = false;
            firstEdgeCity = null;
            choosingSecondCityForPath = false;
            firstPathCity = null;

            while (true) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Lägg till stad");
                dialog.setHeaderText("Ange namnet på staden");

                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()) {
                    cityName = result.get().trim();

                    if (cityName.isEmpty()) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Inget namn har angivits");
                        alert.setContentText("För att gå vidare måste du ange ett namn på en stad");
                        Optional<ButtonType> alertAnswer = alert.showAndWait();

                        if (!alertAnswer.isPresent()) {
                            return;
                        }
                    } else {
                        putNodeHandler = new PutNodeHandler();
                        center.setOnMouseClicked(putNodeHandler);
                        center.setCursor(Cursor.CROSSHAIR);
                        addNodeButton.setDisable(true);
                        break;
                    }
                } else {
                    return;
                }
            }
        }
    }

    /*
     * Startar kantflödet.
     */
    class StartAddEdgeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            /*
             * En första stad måste vara markerad innan kant kan skapas.
             */
            if (selectedCity == null) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Markera först en stad och tryck sedan på Lägg till kant.");
                alert.setHeaderText("Ingen stad markerad");
                alert.showAndWait();
                return;
            }

            /*
             * Den markerade staden sparas som första stad i kanten.
             */
            firstEdgeCity = selectedCity;
            choosingSecondCityForEdge = true;

            /*
             * Om användaren höll på med hitta väg avbryts det läget.
             */
            firstPathCity = null;
            choosingSecondCityForPath = false;

            Alert alert = new Alert(AlertType.INFORMATION,
                    "Klicka på nästa stad som kanten ska gå till.");
            alert.setHeaderText("Välj nästa stad");
            alert.showAndWait();
        }
    }

    /*
     * Körs när användaren klickar på en stad.
     *
     * Metoden hanterar:
     * - vanlig markering
     * - avmarkering
     * - andra stad för kant
     * - slutstad för hitta väg
     */
    private void selectCity(City city) {
        /*
         * Om samma stad klickas igen i vanligt läge avmarkeras den.
         */
        if (!choosingSecondCityForEdge && !choosingSecondCityForPath && selectedCity != null
                && selectedCity.equals(city)) {
            Destination destination = cityDestinations.get(selectedCity);

            if (destination != null) {
                destination.setSelected(false);
            }

            selectedCity = null;
            firstEdgeCity = null;
            choosingSecondCityForEdge = false;
            firstPathCity = null;
            choosingSecondCityForPath = false;
            return;
        }

        /*
         * Om kantläge är aktivt används klicket som andra stad i kanten.
         */
        if (choosingSecondCityForEdge) {
            handleSecondCityForEdge(city);
            return;
        }

        /*
         * Om vägläge är aktivt används klicket som slutstad.
         */
        if (choosingSecondCityForPath) {
            handleSecondCityForPath(city);
            return;
        }

        /*
         * Avmarkera tidigare stad.
         */
        if (selectedCity != null && cityDestinations.containsKey(selectedCity)) {
            cityDestinations.get(selectedCity).setSelected(false);
        }

        /*
         * Markera ny stad.
         */
        selectedCity = city;

        Destination destination = cityDestinations.get(city);

        if (destination != null) {
            destination.setSelected(true);
        }
    }

    /*
     * Hanterar andra staden när en kant ska skapas.
     */
    private void handleSecondCityForEdge(City secondCity) {
        if (firstEdgeCity == null) {
            choosingSecondCityForEdge = false;
            return;
        }

        if (firstEdgeCity.equals(secondCity)) {
            Alert alert = new Alert(AlertType.WARNING, "Du måste välja två olika städer.");
            alert.setHeaderText("Ogiltigt val");
            alert.showAndWait();
            return;
        }

        showEdgeDialog(firstEdgeCity, secondCity);

        choosingSecondCityForEdge = false;
        firstEdgeCity = null;
    }

    /*
     * Dialogruta där användaren anger färdmedel och vikt för kanten.
     */
    private void showEdgeDialog(City from, City to) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lägg till kant");
        dialog.setHeaderText("Skapa resväg mellan " + from.getName() + " och " + to.getName());

        TextField routeNameField = new TextField();
        routeNameField.setPromptText("Exempel: Tåg");

        TextField weightField = new TextField();
        weightField.setPromptText("Exempel: 45");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Färdmedel:"), 0, 0);
        grid.add(routeNameField, 1, 0);
        grid.add(new Label("Vikt/minuter:"), 0, 1);
        grid.add(weightField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        /*
         * Validerar input innan dialogen stängs.
         */
        okButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            String routeName = routeNameField.getText().trim();
            String weightText = weightField.getText().trim();

            if (routeName.isEmpty()) {
                Alert alert = new Alert(AlertType.WARNING, "Du måste ange ett färdmedel.");
                alert.setHeaderText("Ogiltigt färdmedel");
                alert.showAndWait();
                actionEvent.consume();
                return;
            }

            int weight;
            try {
                weight = Integer.parseInt(weightText);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(AlertType.WARNING, "Vikten måste vara ett heltal.");
                alert.setHeaderText("Ogiltig vikt");
                alert.showAndWait();
                actionEvent.consume();
                return;
            }

            if (weight < 0) {
                Alert alert = new Alert(AlertType.WARNING, "Vikten får inte vara negativ.");
                alert.setHeaderText("Ogiltig vikt");
                alert.showAndWait();
                actionEvent.consume();
                return;
            }

            try {
                /*
                 * Skapar kanten i grafen.
                 */
                listGraph.connect(from, to, routeName, weight);

                /*
                 * Ritar om alla kanter så att den nya kanten syns.
                 */
                redrawEdgesFromGraph();

                hasUnsavedChanges = true;
                save.setDisable(false);

            } catch (IllegalStateException e) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Det finns redan en kant mellan de markerade städerna.");
                alert.setHeaderText("Kanten finns redan");
                alert.showAndWait();
                actionEvent.consume();

            } catch (RuntimeException e) {
                Alert alert = new Alert(AlertType.ERROR,
                        "Kanten kunde inte skapas: " + e.getMessage());
                alert.setHeaderText("Fel vid skapande av kant");
                alert.showAndWait();
                actionEvent.consume();
            }
        });

        dialog.showAndWait();
    }

    /*
     * Startar flödet för Hitta väg.
     */
    class StartFindPathHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            if (selectedCity == null) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Markera först en startstad och tryck sedan på Hitta väg.");
                alert.setHeaderText("Ingen startstad markerad");
                alert.showAndWait();
                return;
            }

            /*
             * Den markerade staden blir startstad.
             */
            firstPathCity = selectedCity;
            choosingSecondCityForPath = true;

            /*
             * Om kantläge var aktivt avbryts det.
             */
            firstEdgeCity = null;
            choosingSecondCityForEdge = false;

            Alert alert = new Alert(AlertType.INFORMATION,
                    "Klicka på slutstaden för att söka väg med " + selectedAlgorithmName + ".");
            alert.setHeaderText("Välj slutstad");
            alert.showAndWait();
        }
    }

    /*
     * Hanterar slutstaden för Hitta väg.
     */
    private void handleSecondCityForPath(City secondCity) {
        if (firstPathCity == null) {
            choosingSecondCityForPath = false;
            return;
        }

        if (firstPathCity.equals(secondCity)) {
            Alert alert = new Alert(AlertType.WARNING, "Du måste välja två olika städer.");
            alert.setHeaderText("Ogiltigt val");
            alert.showAndWait();
            return;
        }

        /*
         * Själva vägberäkningen.
         *
         * pathFinder kan vara BFSPathFinder, DFSPathFinder eller DijkstraPathFinder
         * beroende på vad användaren valt.
         */
        Path<City> path = pathFinder.findPath(listGraph, firstPathCity, secondCity);

        choosingSecondCityForPath = false;
        firstPathCity = null;

        if (path == null) {
            Alert alert = new Alert(AlertType.INFORMATION,
                    "Det finns ingen väg mellan de valda städerna med " + selectedAlgorithmName + ".");
            alert.setHeaderText("Ingen väg hittades");
            alert.showAndWait();
            return;
        }

        showPathResult(path);
    }

    /*
     * Visar resultatet av vägberäkningen.
     *
     * Visar:
     * - algoritm
     * - startnod
     * - slutnod
     * - alla noder i vägen
     * - alla kanter i vägen
     * - total vikt
     */
    private void showPathResult(Path<City> path) {
        StringBuilder result = new StringBuilder();

        result.append("Algoritm: ").append(selectedAlgorithmName).append("\n");
        result.append("Start: ").append(path.getStart().getName()).append("\n");
        result.append("Slut: ").append(path.getEnd().getName()).append("\n\n");

        result.append("Noder i vägen:\n");
        for (City city : path.getNodes()) {
            result.append("- ").append(city.getName()).append("\n");
        }

        result.append("\nKanter i vägen:\n");

        List<City> nodes = path.getNodes();
        List<Edge<City>> edges = path.getEdges();

        for (int i = 0; i < edges.size(); i++) {
            Edge<City> edge = edges.get(i);
            String fromName = i < nodes.size() ? nodes.get(i).getName() : "?";
            String toName = edge.getDestination().getName();

            result.append("- ")
                    .append(fromName)
                    .append(" → ")
                    .append(toName)
                    .append(" med ")
                    .append(edge.getName())
                    .append(", vikt: ")
                    .append(edge.getWeight())
                    .append("\n");
        }

        result.append("\nTotal vikt: ").append(path.getTotalWeight());

        TextArea textArea = new TextArea(result.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(450);
        textArea.setPrefHeight(300);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Hittad väg");
        alert.setHeaderText("Väg hittad med " + selectedAlgorithmName);
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    /*
     * Tar bort markerad nod.
     */
    class RemoveNodeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            if (selectedCity == null) {
                Alert alert = new Alert(
                        AlertType.WARNING,
                        "Du måste markera en stad innan du kan ta bort den.");
                alert.setHeaderText("Ingen stad markerad");
                alert.showAndWait();
                return;
            }

            try {
                City cityToRemove = selectedCity;

                /*
                 * Tar bort staden från grafen.
                 * ListGraph.remove ska också ta bort kanter kopplade till staden.
                 */
                listGraph.remove(cityToRemove);

                /*
                 * Tar bort den grafiska noden från kartan.
                 */
                Destination destination = cityDestinations.remove(cityToRemove);

                if (destination != null) {
                    center.getChildren().remove(destination);
                }

                /*
                 * Nollställer markeringslägen.
                 */
                selectedCity = null;
                firstEdgeCity = null;
                choosingSecondCityForEdge = false;
                firstPathCity = null;
                choosingSecondCityForPath = false;

                /*
                 * Ritar om kanterna så att kanter till den borttagna noden försvinner.
                 */
                redrawEdgesFromGraph();

                hasUnsavedChanges = true;
                save.setDisable(false);

            } catch (RuntimeException e) {
                Alert alert = new Alert(
                        AlertType.ERROR,
                        "Staden kunde inte tas bort: " + e.getMessage());
                alert.setHeaderText("Fel vid borttagning");
                alert.showAndWait();
            }
        }
    }

    /*
     * Kopplar klick och flyttning till en Destination.
     */
    private void attachDestinationHandlers(City city, Destination destination) {
        destination.setOnMouseClicked(mouseEvent -> {
            mouseEvent.consume();
            selectCity(city);
        });

        /*
         * När noden flyttas:
         * - uppdateras City-koordinaterna
         * - alla kanter ritas om
         */
        destination.setOnPositionChanged(() -> {
            city.setX(destination.getX());
            city.setY(destination.getY());
            redrawEdgesFromGraph();
            hasUnsavedChanges = true;
            save.setDisable(false);
        });
    }

    /*
     * Ritar om alla kanter i grafen.
     */
    private void redrawEdgesFromGraph() {
        center.getChildren().removeIf(node -> node instanceof Line);

        for (City city : listGraph.getNodes()) {
            for (Edge<City> edge : listGraph.getEdgesFrom(city)) {
                City destination = edge.getDestination();

                /*
                 * Eftersom grafen är oriktad finns samma kant från båda håll.
                 * Denna kontroll gör att kanten bara ritas en gång.
                 */
                if (city.getName().compareTo(destination.getName()) < 0) {
                    drawEdge(city, destination);
                }
            }
        }
    }

    /*
     * Ritar en linje mellan två städer.
     */
    private void drawEdge(City from, City to) {
        Destination fromDestination = cityDestinations.get(from);
        Destination toDestination = cityDestinations.get(to);

        if (fromDestination == null || toDestination == null) {
            return;
        }

        /*
         * Linjen kopplas till nålens centrum.
         */
        Line line = new Line(
                fromDestination.getConnectionX(),
                fromDestination.getConnectionY(),
                toDestination.getConnectionX(),
                toDestination.getConnectionY());

        line.setStrokeWidth(3);
        line.setMouseTransparent(true);

        /*
         * Linjen läggs bakom noderna men ovanför kartan.
         */
        int index = Math.min(1, center.getChildren().size());
        center.getChildren().add(index, line);
    }

    /*
     * Ritar om alla noder och kanter efter att ett projekt har öppnats.
     */
    private void redrawCitiesFromGraph() {
        center.getChildren().removeIf(node -> node instanceof Destination);
        center.getChildren().removeIf(node -> node instanceof Line);

        cityDestinations.clear();

        selectedCity = null;
        firstEdgeCity = null;
        choosingSecondCityForEdge = false;
        firstPathCity = null;
        choosingSecondCityForPath = false;

        for (City city : listGraph.getNodes()) {
            Destination destination = new Destination(city.getName(), city.getX(), city.getY());
            attachDestinationHandlers(city, destination);
            cityDestinations.put(city, destination);
            center.getChildren().add(destination);
        }

        redrawEdgesFromGraph();
    }

    class OpenHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            fileChooser.setInitialDirectory(new File("./src/main/resources/saved"));
            File fileName = fileChooser.showOpenDialog(stage);
            if (fileName == null) return;

            listGraph = new ListGraph<>();
            cityDestinations.clear();
            center.getChildren().clear();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                imagePath = reader.readLine();
                int numberOfNodes = Integer.parseInt(reader.readLine());
                Map<String, City> cities = new HashMap<>();
                for (int i = 0; i < numberOfNodes; i++) {
                    String[] node = reader.readLine().split(" ");
                    City city = new City(node[0], Double.parseDouble(node[1]), Double.parseDouble(node[2]));
                    listGraph.add(city);
                    cities.put(node[0], city);    
                }

                for (int i = 0; i < numberOfNodes; i++) {
                    String[] node = reader.readLine().split(" ");
                    City city = cities.get(node[0]);
                    int numberOfEdges = Integer.parseInt(reader.readLine());
                    for (int j = 0; j < numberOfEdges; j++) {
                        String[] edge = reader.readLine().split(" ");
                        City destination = cities.get(edge[1]);
                        try {
                            listGraph.connect(city, destination, edge[5], Integer.parseInt(edge[7]));
                        } catch (IllegalStateException e) {}
                    }
                }
                reader.close();
                image = new ImageView(new Image(imagePath));
                //image = new ImageView(new Image(App.class.getResourceAsStream(imagePath)));
                center.getChildren().add(image);
                redrawCitiesFromGraph();
                root.setRight(right);
                stage.sizeToScene();
                obsList.clear();
                obsList.addAll(listGraph.getNodes().toString());

                hasUnsavedChanges = false;
                save.setDisable(true);

            } catch (IOException | NumberFormatException | StringIndexOutOfBoundsException e) {
                Alert alert = new Alert(AlertType.ERROR, "Fel vid inläsning: Filen är skadad eller har fel format.");
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            fileChooser.setInitialDirectory(new File("./src/main/resources/saved"));
            File fileName = fileChooser.showSaveDialog(stage);

            if (fileName == null) {
                return;
            }
            try { 
                PrintWriter writer = new PrintWriter(new FileWriter(fileName));
                writer.println(imagePath);
                writer.print(listGraph.toString());
                writer.close();
                hasUnsavedChanges = false;
                save.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();

                Alert alert = new Alert(AlertType.ERROR, "Projektet kunde inte sparas.");
                alert.setHeaderText("Fel vid sparning");
                alert.showAndWait();
            }
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            try {
                BufferedImage snapshot = SwingFXUtils.fromFXImage(center.snapshot(null, null), null);
                ImageIO.write(snapshot, "png", new File("/resources/maps/capture.png"));

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION,
                        "En bild av kartan finns nu sparad i rotmappen.");
                alert.setHeaderText("Sparat!");
                alert.setTitle("Sparar bild...");
                alert.show();

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error");
                alert.showAndWait();
            }
        }
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}