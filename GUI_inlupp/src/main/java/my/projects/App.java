package my.projects;

import java.util.List;

import javax.smartcardio.Card;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class App extends Application {

    private Insets femPx = new Insets(5);
    private String font = "-fx-font-size: 14;";

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        Pane center = new Pane();
        root.setCenter(center);

        ImageView image = new ImageView(new Image(App.class.getResourceAsStream("/sverigekarta2.jpg")));
        center.getChildren().add(image);

        VBox right = new VBox(10);
        Label meny = new Label("Meny");
        Button ny = new Button("Ny");
        Button öppna = new Button("Öppna");
        Button spara = new Button("Spara");
        Button avsluta = new Button("Avsluta");
        Button väljSökAlgoritm = new Button("Välj\nSökalgoritm");
        List<Button> rightButtons = List.of(ny, öppna, spara, avsluta, väljSökAlgoritm);
        for (Button button : rightButtons) {
            button.setPrefWidth(90);
            button.setTextAlignment(TextAlignment.CENTER);
            button.setOnMouseClicked(new ClickHandler());
        }
        meny.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        right.getChildren().addAll(meny, ny, öppna, spara, avsluta, väljSökAlgoritm);
        right.setAlignment(Pos.TOP_CENTER);
        right.setPadding(femPx);
        right.setStyle(font);
        root.setRight(right);

        HBox bottom = new HBox(10);
        Button läggTillNod = new Button("Lägg till nod");
        Button taBortNod = new Button("Ta bort nod");
        Button läggTillKant = new Button("Lägg till kant");
        Button taBortKant = new Button("Ta bort kant");
        List<Button> bottomButtons = List.of(läggTillNod, taBortNod, läggTillKant, taBortKant);
        for (Button button : bottomButtons) {
            button.setOnMouseClicked(new ClickHandler());
        }
        bottom.getChildren().addAll(läggTillNod, taBortNod, läggTillKant, taBortKant);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(femPx);
        bottom.setStyle(font);
        root.setBottom(bottom);

        Scene scene = new Scene(root);
        stage.setTitle("Reseplanerare");
        stage.setScene(scene);
        stage.show();
    }

    public class ClickHandler implements EventHandler<MouseEvent> {
        /*public void handle(MouseEvent event) {
            Button pressed = (Button) event.getSource();
            if (pressed)

            GridPane root = new GridPane();
            root.setPrefSize(500, 500);
            
            
            Scene scene = new Scene(root);
            Stage popupWindow = new Stage();
            popupWindow.setScene(scene);
            popupWindow.show();
        }*/
    }
    

    public static void main( String[] args ) {
        launch(args);
    }
}
