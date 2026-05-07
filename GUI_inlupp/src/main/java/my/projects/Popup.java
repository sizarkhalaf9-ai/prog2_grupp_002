package my.projects;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public abstract class Popup {
    
    public Popup() {
        GridPane root = new GridPane();
        root.setPrefSize(500, 500);
        
        
        Scene scene = new Scene(root);
        Stage popupWindow = new Stage();
        popupWindow.setScene(scene);
        popupWindow.show();
    }

    public boolean hasSameButtonText() { return true; }
}
