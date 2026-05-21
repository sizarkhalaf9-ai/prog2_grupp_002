package se.su.inlupp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
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

    private Button addEdgeButton;

    private FileChooser fileChooser = new FileChooser();
    private boolean hasUnsavedChanges = false;

    private Button saveNewNode = new Button("Spara nod");

    private double newNodeX;
    private double newNodeY;

    private Destination pendingNode;

    private TravelPlannerModel model = new TravelPlannerModel();

    private City selectedCity;

    private Map<City, Destination> cityDestinations = new HashMap<>();

    private ObservableList<String> obsList = FXCollections.observableArrayList();
    private Graph<City> listGraph = new ListGraph<City>();
    private Destination needle;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        root = new BorderPane();

        center = new Pane();
        image = new ImageView(new Image(App.class.getResourceAsStream("/empty.png")));
        center.getChildren().add(image);

        center.setPickOnBounds(true);

        root.setCenter(center);

        Menu file = new Menu("Arkiv");
        MenuItem newMap = new MenuItem("Ny");
        newMap.setOnAction(new NewMapHandler());
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

        Button removeNodeButton = new Button("Ta bort nod");

        addEdgeButton = new Button("Lägg till kant");

        List<Button> rightButtons = List.of(
                addNodeButton,
                saveNewNode,
                removeNodeButton,
                addEdgeButton);
        for (Button button : rightButtons) {
            button.setPrefWidth(110);
            button.setTextAlignment(TextAlignment.CENTER);
        }

        MenuButton algorithmButton = new MenuButton("Välj\nSökalgoritm");
        algorithmButton.setPrefWidth(110);

        MenuItem dfs = new MenuItem("Djupet först-sökning");
        MenuItem bfs = new MenuItem("Bredden först-sökning");
        MenuItem dijkstra = new MenuItem("Dijkstras algoritm");

        algorithmButton.getItems().addAll(dfs, bfs, dijkstra);

        right.getChildren().addAll(
                editLabel,
                addNodeButton,
                saveNewNode,
                removeNodeButton,
                addEdgeButton,
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

    public class NewMapChooser extends Dialog<ButtonType> {
        public static final ButtonType KARTA1 = new ButtonType("Sverigekarta med angränsande länder");
        public static final ButtonType KARTA2 = new ButtonType("Sverigekarta utan grannländer");

        public NewMapChooser() {
            setTitle("Välj karta");
            setHeaderText("Välj en karta som du vill jobba med");
            getDialogPane().getButtonTypes().addAll(KARTA1, KARTA2, ButtonType.CANCEL);
        }
    }

    class NewMapHandler implements EventHandler<ActionEvent> {
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
            NewMapChooser mapChooser = new NewMapChooser();
            Optional<ButtonType> mapChoice = mapChooser.showAndWait();
            if (mapChoice.isPresent() && mapChoice.get() != ButtonType.CANCEL) {
                center.getChildren().clear();
                listGraph = new ListGraph<>();
                cityDestinations.clear();
                hasUnsavedChanges = false;
                save.setDisable(true);

                if (mapChoice.get() == NewMapChooser.KARTA1) {
                    image = new ImageView(new Image(App.class.getResourceAsStream("/maps/sverigekarta2.jpg")));
                    imagePath = "/maps/sverigekarta2.jpg";
                } else if (mapChoice.get() == NewMapChooser.KARTA2) {
                    image = new ImageView(new Image(App.class.getResourceAsStream("/maps/sverigekarta1.jpg")));
                    imagePath = "/maps/sverigekarta1.jpg";
                }
                center.getChildren().add(image);

                root.setRight(right);
                stage.sizeToScene();
            }
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

    class SaveNewNodeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            City city = new City(cityName, needle.getX(), needle.getY());

            if (listGraph.hasNode(city)) {
                listGraph.remove(city);
            }
            if (needle != null) {
                needle.lock();
                needle.setOnMouseClicked(mouseEvent -> {
                    mouseEvent.consume();
                    selectCity(city);
                });
                listGraph.add(city);
                cityDestinations.put(city, needle);

                needle = null;
            }
            center.setOnMouseClicked(null);
            center.setCursor(Cursor.DEFAULT);
            addNodeButton.setDisable(false);
            hasUnsavedChanges = true;
            save.setDisable(false);
            saveNewNode.setDisable(true);

            System.out.println(listGraph.toString());

        }
    }

    class PutNodeHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            // newNodeX = event.getX();
            // newNodeY = event.getY();

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

    class AddNodeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
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
                    alert.showAndWait();
                } else {
                    putNodeHandler = new PutNodeHandler();
                    center.setOnMouseClicked(putNodeHandler);
                    center.setCursor(Cursor.CROSSHAIR);
                    addNodeButton.setDisable(true);
                }
            }
        }
    }

    class AddEdgeHandler{
        public void handle (ActionEvent event){
       
        }
    }

    private void selectCity(City city) {
        if (selectedCity != null && cityDestinations.containsKey(selectedCity)) {
            cityDestinations.get(selectedCity).setStyle("");
        }

        selectedCity = city;

        Destination destination = cityDestinations.get(city);

        if (destination != null) {
            destination.setStyle(
                    "-fx-border-color: red; " +
                            "-fx-border-width: 3; " +
                            "-fx-border-radius: 4;");
        }
    }

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
                listGraph.remove(selectedCity);

                Destination destination = cityDestinations.remove(selectedCity);

                if (destination != null) {
                    center.getChildren().remove(destination);
                }

                selectedCity = null;
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

    private void redrawCitiesFromGraph() {
        center.getChildren().removeIf(node -> node instanceof Destination);

        cityDestinations.clear();
        selectedCity = null;

        for (City city : listGraph.getNodes()) {
            Destination destination = new Destination(city.getName(), city.getX(), city.getY());

            destination.setOnMouseClicked(mouseEvent -> {
                mouseEvent.consume();
                selectCity(city);
            });

            cityDestinations.put(city, destination);
            center.getChildren().add(destination);
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            fileChooser.setInitialDirectory(new File("."));
            File fileName = fileChooser.showSaveDialog(stage);
            if (fileName == null) {
                return;
            }

            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
                oos.writeObject(imagePath);
                oos.writeObject(listGraph);
                oos.close();

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
                BufferedImage image = SwingFXUtils.fromFXImage(center.snapshot(null, null), null);
                ImageIO.write(image, "png", new File("/resources/maps/capture.png"));

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

    class OpenHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            fileChooser.setInitialDirectory(new File("."));

            File fileName = fileChooser.showOpenDialog(stage);

            if (fileName == null) {
                return;
            }

            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
                imagePath = (String) inputStream.readObject();
                listGraph = (Graph<City>) inputStream.readObject();
                inputStream.close();

                image = new ImageView(new Image(App.class.getResourceAsStream(imagePath)));
                center.getChildren().clear();
                center.getChildren().add(image);
                root.setRight(right);
                stage.sizeToScene();
                redrawCitiesFromGraph();

                obsList.clear();
                obsList.addAll(listGraph.getNodes().toString());

                hasUnsavedChanges = false;
                save.setDisable(true);

            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR, "Filen kunde inte hittas");
                alert.showAndWait();
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();

                Alert alert = new Alert(AlertType.ERROR, "Filen kunde inte läsas in.");
                alert.setHeaderText("Fel vid öppning");
                alert.showAndWait();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}