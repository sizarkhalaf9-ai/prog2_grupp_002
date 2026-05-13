package my.projects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class App extends Application {

    private Pane center;
    private Insets femPx = new Insets(5);
    private String font = "-fx-font: 12px 'Verdana';";
    private Button addNode, saveButton;
    private AddNodeHandler addNodeHandler;
    private NewNodeHandler newNodeHandler;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        center = new Pane();
        root.setCenter(center);

        ImageView image = new ImageView(new Image(App.class.getResourceAsStream("/sverigekarta2.jpg")));
        center.getChildren().add(image);

        VBox right = new VBox(10);
        Label menuLabel = new Label("Meny");
        menuLabel.setStyle("-fx-font: 22 'Copperplate'; -fx-font-weight: bold;");
        Button newButton = new Button("Ny");
        Button openButton = new Button("Öppna");
        saveButton = new Button("Spara karta");
        saveButton.setOnAction(new SaveButtonHandler());
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
        addNode = new Button("Lägg till nod");
        newNodeHandler = new NewNodeHandler();
        addNode.setOnAction(newNodeHandler);
        Button taBortNod = new Button("Ta bort nod");
        Button läggTillKant = new Button("Lägg till kant");
        Button taBortKant = new Button("Ta bort kant");
        /*List<Button> bottomButtons = List.of(läggTillNod, taBortNod, läggTillKant, taBortKant);
        for (Button button : bottomButtons) {
            button.setOnMouseClicked(new ClickHandler());
        }*/
        bottom.getChildren().addAll(addNode, taBortNod, läggTillKant, taBortKant);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(femPx);
        bottom.setStyle(font);
        root.setBottom(bottom);

        Scene scene = new Scene(root);
        stage.setTitle("Reseplanerare");
        stage.setScene(scene);
        stage.show();
    }

    class AddNodeHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();

            Destination needle = new Destination(x, y);
            center.getChildren().add(needle);

            //Återställ allt när needle är tillagd i center
            center.setOnMouseClicked(null);
            center.setCursor(Cursor.DEFAULT);
            addNode.setDisable(false);
        }
    }

    class NewNodeHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            addNodeHandler = new AddNodeHandler();
            center.setOnMouseClicked(addNodeHandler);
            center.setCursor(Cursor.CROSSHAIR);
            addNode.setDisable(true);
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
    
    class SaveButtonHandler implements EventHandler<ActionEvent> {
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

    public static void main( String[] args ) {
        launch(args);
    }
}
