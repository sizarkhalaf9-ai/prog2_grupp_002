package se.su.inlupp;

import java.awt.Desktop.Action;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.su.inlupp.App.OpenHandler;
import se.su.inlupp.App.SaveHandler;
import se.su.inlupp.App.SaveImageHandler;

public class AppGammal extends Application {

    private Stage stage;
    private Pane center;
    private Insets femPx = new Insets(5);
    private String font = "-fx-font: 12px 'Verdana';";
    private Button addNodeButton, saveButton;
    private PutNodeHandler putNodeHandler;
    private AddNodeHandler addNodeHandler;
    private FileChooser fileChooser = new FileChooser();
    private boolean hasUnsavedChanges = false;

    private Graph<City> listGraph = new ListGraph<City>();

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        BorderPane root = new BorderPane();
        
        center = new Pane();
        root.setCenter(center);
        ImageView image = new ImageView(new Image(App.class.getResourceAsStream("/sverigekarta2.jpg")));
        center.getChildren().add(image);

        Menu file = new Menu("Arkiv");
        MenuItem neew =  new MenuItem("Ny");
        //neew.setOnAction(new NewHandler());
        MenuItem open = new MenuItem("Öppna...");
        OpenHandler openHandler = new OpenHandler();
        open.setOnAction(openHandler);
        MenuItem save = new MenuItem("Spara...");
        save.setOnAction(new SaveHandler());
        MenuItem exit = new MenuItem("Avsluta");
        exit.setOnAction(new CloseWindowHandler());
        file.getItems().addAll(neew, open, save, exit);

        Menu edit = new Menu("Redigera");
        Menu add = new Menu("Lägg till");
        Menu remove = new Menu("Ta bort");
        MenuItem addNode = new MenuItem("Nod");
        MenuItem addEdge = new MenuItem("Kant");
        MenuItem removeNode = new MenuItem("Nod");
        MenuItem removeEdge = new MenuItem("Kant");
        edit.getItems().addAll(add, remove);
        add.getItems().addAll(addNode, addEdge);
        remove.getItems().addAll(removeNode, removeEdge);
        
        MenuBar top = new MenuBar(file, edit);
        root.setTop(top);

        VBox right = new VBox(10);
        Label menuLabel = new Label("Meny");
        menuLabel.setStyle("-fx-font: 22 'Copperplate'; -fx-font-weight: bold;");
        Button newButton = new Button("Ny");
        Button openButton = new Button("Öppna");
        openButton.setOnAction(openHandler);
        saveButton = new Button("Spara karta");
        saveButton.setOnAction(new SaveImageHandler());
        Button exitButton = new Button("Avsluta");
        Button algorithmButton = new Button("Välj\nSökalgoritm");
        List<Button> rightButtons = List.of(newButton, openButton, saveButton, exitButton, algorithmButton);
        for (Button button : rightButtons) {
            button.setPrefWidth(90);
            button.setTextAlignment(TextAlignment.CENTER);
        }
        right.getChildren().addAll(menuLabel, newButton, openButton, saveButton, exitButton, algorithmButton);
        right.setAlignment(Pos.TOP_CENTER);
        right.setPadding(femPx);
        right.setStyle(font);
        root.setRight(right);

        HBox bottom = new HBox(10);
        addNodeButton = new Button("Lägg till nod");
        addNodeHandler = new AddNodeHandler();
        addNodeButton.setOnAction(addNodeHandler);
        Button taBortNod = new Button("Ta bort nod");
        Button läggTillKant = new Button("Lägg till kant");
        Button taBortKant = new Button("Ta bort kant");
        /*List<Button> bottomButtons = List.of(läggTillNod, taBortNod, läggTillKant, taBortKant);
        for (Button button : bottomButtons) {
            button.setOnMouseClicked(new ClickHandler());
        }*/
        bottom.getChildren().addAll(addNodeButton, taBortNod, läggTillKant, taBortKant);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(femPx);
        bottom.setStyle(font);
        root.setBottom(bottom);

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
                Alert alert = new Alert(AlertType.CONFIRMATION, "Det finns osparade ändringar. Vill du avsluta ändå?");
                alert.setHeaderText("Osparade ändringar");
                alert.setTitle("Varning");
                Optional<ButtonType> clicked = alert.showAndWait();
                if (clicked.isPresent() && clicked.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                }
            }
        }
    }
    class PutNodeHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();

            Destination needle = new Destination(x, y);
            center.getChildren().add(needle);

            hasUnsavedChanges = true;

            //Återställ allt när needle är tillagd i center
            center.setOnMouseClicked(null);
            center.setCursor(Cursor.DEFAULT);
            addNodeButton.setDisable(false);    
        }
    }

    class AddNodeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            putNodeHandler = new PutNodeHandler();
            center.setOnMouseClicked(putNodeHandler);
            center.setCursor(Cursor.CROSSHAIR);
            addNodeButton.setDisable(true);
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

    /*class NewHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event) {
            if (hasUnsavedChanges) {
                Alert alert = new Alert(AlertType.CONFIRMATION, "Det finns osparade ändringar. Vill du avsluta ändå?");
                alert.setHeaderText("Osparade ändringar");
                alert.setTitle("Varning");
                Optional<ButtonType> clicked = alert.showAndWait();
                if (clicked.isPresent() && clicked.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                } else if (clicked.get().equals(ButtonType.OK)) {
                    SaveHandler save = new SaveHandler();
                    fileChooser.setInitialDirectory(new File("."));
                    File fileName = fileChooser.showSaveDialog(stage);
                }
            } else { 
                //cleara alla nålar och allt innehåll i listGraph etc eller skapa ny på annat sätt
            }
        }
    }*/

    class SaveHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            fileChooser.setInitialDirectory(new File("."));
            File fileName = fileChooser.showSaveDialog(stage);
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
                oos.writeObject(listGraph);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            hasUnsavedChanges = false;
        }
    }
    
    class SaveImageHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            try {
                BufferedImage image = SwingFXUtils.fromFXImage(center.snapshot(null, null), null);
                ImageIO.write(image, "png", new File("capture.png"));
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "En bild av kartan finns nu sparad i rotmappen.");
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
            /*try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
                dataMap = (Map) inputStream.readObject();
                ois.close();
                obsList.clear();
                obsList.addAll(dataMap.keySet());
            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR, "Filen kunde inte hittas");
            }*/
        }
    }


    public static void main( String[] args ) {
        launch(args);
    }
}
