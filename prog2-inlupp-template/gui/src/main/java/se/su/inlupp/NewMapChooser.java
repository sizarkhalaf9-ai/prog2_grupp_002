package se.su.inlupp;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

public class NewMapChooser extends Dialog<ButtonType> {
    


    public NewMapChooser() {
        setTitle("Välj karta");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        



    }
}
