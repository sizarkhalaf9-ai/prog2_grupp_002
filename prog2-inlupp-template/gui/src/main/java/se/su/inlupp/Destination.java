package se.su.inlupp;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Destination extends Pane {

    private double startX;
    private double startY;
    private double newX;
    private double newY;
    private String name;

    private Runnable positionChanged;

    public Destination(String name, double x, double y) {
        this.name = name;
        newX = x;
        newY = y;
        relocate(newX - 10, newY - 19);

        ImageView image = new ImageView(new Image(Destination.class.getResourceAsStream("/nål.png")));
        image.setFitHeight(20);
        image.setFitWidth(20);
        getChildren().add(image);
        setPrefSize(20, 20);

        setOnMousePressed(new StartDragHandler());
        setOnMouseDragged(new DragHandler());
        setOnKeyPressed(new KeyHandler());
    }

    public void lock() {
        setOnMousePressed(null);
        setOnMouseDragged(null);
        setOnKeyPressed(null);
        setCursor(Cursor.DEFAULT);
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return newX;
    }

    public double getY() {
        return newY;
    }

    public void setOnPositionChanged(Runnable positionChanged) {
        this.positionChanged = positionChanged;
    }

    private void notifyPositionChanged() {
        if (positionChanged != null) {
            positionChanged.run();
        }
    }

    class DragHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            newX = getLayoutX() + event.getX() - startX;
            newY = getLayoutY() + event.getY() - startY;
            setCursor(Cursor.CLOSED_HAND);
            relocate(newX, newY);

            notifyPositionChanged();
        }
    }

    class StartDragHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            startX = event.getX();
            startY = event.getY();
        }
    }

    class KeyHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            double x = getLayoutX();
            double y = getLayoutY();
            switch (event.getCode()) {
                case DOWN:
                    y += 1;
                    break;
                case UP:
                    y -= 1;
                    break;
                case RIGHT:
                    x += 1;
                    break;
                case LEFT:
                    x -= 1;
                    break;
            }
            event.consume(); // när händelsen har inträffat måste den konsumeras och ersättas av annan
                             // händelse

            notifyPositionChanged();

            relocate(x, y);
        }
    }
}
