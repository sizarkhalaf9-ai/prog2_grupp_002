package se.su.inlupp;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class NewMapChooser extends Dialog<ButtonType> {
    
    public static final ButtonType KARTA1 = new ButtonType("Sverigekarta med angränsande länder");
    public static final ButtonType KARTA2 = new ButtonType("Sverigekarta utan grannländer");

    public NewMapChooser() {
        setTitle("Välj karta");
        setHeaderText("Välj en karta som du vill jobba med");
        
        getDialogPane().getButtonTypes().addAll(KARTA1, KARTA2, ButtonType.CANCEL);
    }
}