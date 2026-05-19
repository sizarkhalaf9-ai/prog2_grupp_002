package se.su.inlupp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

    private Stage stage;
    private Pane center;
    private Insets femPx = new Insets(5);
    private String font = "-fx-font: 12px 'Verdana';";

    private Button addNodeButton;
    private Button saveButton;

    private FileChooser fileChooser = new FileChooser();
    private boolean hasUnsavedChanges = false;

    private TextField newNodeName = new TextField();
    private Button saveNewNode = new Button("Spara nod");

    private double newNodeX;
    private double newNodeY;

    //private Destination pendingNode;

    private City selectedCity;

    private Map<City, Destination> cityDestinations = new HashMap<>();

    private ObservableList<String> obsList = FXCollections.observableArrayList();
    private Graph<City> listGraph = new ListGraph<City>();
    private Destination needle;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        BorderPane root = new BorderPane();

        center = new Pane();
        root.setCenter(center);

        ImageView image = new ImageView(new Image(App.class.getResourceAsStream("/sverigekarta2.jpg")));
        center.getChildren().add(image);

        Menu file = new Menu("Arkiv");

        MenuItem neew = new MenuItem("Ny");

        MenuItem open = new MenuItem("Öppna...");
        OpenHandler openHandler = new OpenHandler();
        open.setOnAction(openHandler);

        Menu save = new Menu("Spara...");
        MenuItem saveGraph = new MenuItem("Projekt");
        MenuItem saveImage = new MenuItem("Bild");
        save.getItems().addAll(saveGraph, saveImage);

        saveGraph.setOnAction(new SaveHandler());
        saveImage.setOnAction(new SaveImageHandler());

        MenuItem exit = new MenuItem("Avsluta");
        exit.setOnAction(new CloseWindowHandler());

        file.getItems().addAll(neew, open, save, exit);

        /*Menu edit = new Menu("Redigera");
        Menu add = new Menu("Lägg till");
        Menu remove = new Menu("Ta bort");

        MenuItem addNode = new MenuItem("Nod");
        addNode.setOnAction(new AddNodeHandler());

        MenuItem addEdge = new MenuItem("Kant");

        MenuItem removeNode = new MenuItem("Nod");
        removeNode.setOnAction(new RemoveNodeHandler());

        MenuItem removeEdge = new MenuItem("Kant");

        edit.getItems().addAll(add, remove);
        add.getItems().addAll(addNode, addEdge);
        remove.getItems().addAll(removeNode, removeEdge);*/
        
        MenuBar top = new MenuBar(file);
        root.setTop(top);

        VBox right = new VBox(10);

        Label editLabel = new Label("Redigera");
        editLabel.setStyle("-fx-font: 22 'Copperplate'; -fx-font-weight: bold;");

        addNodeButton = new Button("Lägg till stad");
        addNodeButton.setOnAction(new AddNodeHandler());

        Label newNodeNameLabel = new Label("Ange stadens namn");

        saveNewNode.setDisable(true);

        Button removeNodeButton = new Button("Ta bort nod");
        removeNodeButton.setOnAction(new RemoveNodeHandler());

        Button openButton = new Button("Öppna");
        openButton.setOnAction(openHandler);

        saveButton = new Button("Spara karta");
        saveButton.setOnAction(new SaveImageHandler());

        Button exitButton = new Button("Avsluta");
        exitButton.setOnAction(new CloseWindowHandler());

        List<Button> rightButtons = List.of(
                addNodeButton,
                removeNodeButton,
                openButton,
                saveButton,
                exitButton);

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
                newNodeNameLabel,
                newNodeName,
                saveNewNode,
                removeNodeButton,
                openButton,
                saveButton,
                exitButton,
                algorithmButton);

        right.setAlignment(Pos.TOP_CENTER);
        right.setPadding(femPx);
        right.setStyle(font);

        root.setRight(right);

        Scene scene = new Scene(root);

        stage.setTitle("Reseplanerare");
        stage.setScene(scene);
        stage.setOnCloseRequest(new ExitHandler());
        stage.show();
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
            if (newNodeName.getText().trim().isEmpty()) { 
                Alert alert = new Alert(AlertType.WARNING, "Du måste ange ett namn på staden");
                alert.showAndWait();
            } else {
                City city = new City(newNodeName.getText(), needle.getX(), needle.getY());
                cityDestinations.put(city, needle);

                if (listGraph.hasNode(city)) {
                    listGraph.remove(city);
                    //ta bort tillhörande nål
                    //Alternativt dialogruta som varnar om att staden redan finns
                }
                if (needle != null) {
                    needle.setOnMouseClicked(mouseEvent -> {
                        mouseEvent.consume();
                        selectCity(city);
                        });
                listGraph.add(city);

                needle = null;
                }
                center.setOnMouseClicked(null);
                center.setCursor(Cursor.DEFAULT);
                addNodeButton.setDisable(false);
                newNodeName.clear();
                hasUnsavedChanges = true;

                System.out.println(listGraph.toString());
            }
        }
    }

    class PutNodeHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            newNodeX = event.getX();
            newNodeY = event.getY();

            needle = new Destination(newNodeX, newNodeY);
            if (newNodeName.getText().trim().isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR, "Du måste ange ett namn på staden.");
                alert.showAndWait();
            } else {
                center.getChildren().add(needle);
            }

            hasUnsavedChanges = true;
            saveNewNode.setDisable(false);
            saveNewNode.setOnAction(new SaveNewNodeHandler());
        }
    }

    class AddNodeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            PutNodeHandler putNodeHandler = new PutNodeHandler();

            center.setOnMouseClicked(putNodeHandler);
            center.setCursor(Cursor.CROSSHAIR);
            addNodeButton.setDisable(true);
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
            Destination destination = new Destination(city.getX(), city.getY());

            destination.setOnMouseClicked(mouseEvent -> {
                mouseEvent.consume();
                selectCity(city);
            });

            cityDestinations.put(city, destination);
            center.getChildren().add(destination);
        }
    }

    class PopupHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();

            GridPane root = new GridPane();
            root.setPrefSize(220, 340);

            Scene scene = new Scene(root);
            Stage popupWindow = new Stage();

            popupWindow.setScene(scene);
            popupWindow.show();
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
                oos.writeObject(listGraph);
                oos.close();

                hasUnsavedChanges = false;

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
                ImageIO.write(image, "png", new File("capture.png"));

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
                listGraph = (Graph<City>) inputStream.readObject();
                inputStream.close();

                redrawCitiesFromGraph();

                obsList.clear();
                obsList.addAll(listGraph.getNodes().toString());

                hasUnsavedChanges = false;

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

    public static void main( String[] args ) {
        launch(args);
    }
}