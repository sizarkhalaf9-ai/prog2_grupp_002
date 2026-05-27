package se.su.inlupp;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class Destination extends Pane {

    /*
     * Dessa konstanter används för att kontrollera storlek och placering
     * av noden, nålen, namnet och markeringsrutan.
     */
    private static final double NODE_WIDTH = 80;
    private static final double NODE_HEIGHT = 45;
    private static final double NEEDLE_SIZE = 20;
    private static final double MARKER_BOX_SIZE = 24;

    /*
     * NEEDLE_CENTER_X och NEEDLE_CENTER_Y anger var nålens centrum är
     * inuti hela Destination-komponenten.
     *
     * Detta används när vi ritar linjer mellan noder.
     * Linjen ska gå till själva nålen, inte till texten under noden.
     */
    private static final double NEEDLE_CENTER_X = NODE_WIDTH / 2;
    private static final double NEEDLE_CENTER_Y = NEEDLE_SIZE / 2 + 13;

    private double startX;
    private double startY;
    private double newX;
    private double newY;

    private String name;

    /*
     * positionChanged är en callback.
     *
     * App.java sätter denna callback.
     * När noden flyttas med mus eller piltangenter körs callbacken.
     * Då kan App.java rita om alla kantlinjer.
     */
    private Runnable positionChanged;

    /*
     * markerBox är den lilla rutan som bara innehåller nålen.
     *
     * Tidigare sattes röd markering på hela Destination,
     * alltså både nål och namn. Då blev den röda rutan för stor.
     *
     * Nu sätter vi röd ram bara på markerBox.
     */
    private StackPane markerBox;

    public Destination(String name, double x, double y) {
        this.name = name;
        newX = x;
        newY = y;

        /*
         * Flyttar hela Destination så att nålens centrum hamnar på newX/newY.
         */
        relocate(newX - NEEDLE_CENTER_X, newY - NEEDLE_CENTER_Y);

        /*
         * Laddar nålbilden från resources.
         * Filen ska ligga i resources och heta nål.png.
         */
        ImageView image = new ImageView(new Image(Destination.class.getResourceAsStream("/nål.png")));
        image.setFitHeight(NEEDLE_SIZE);
        image.setFitWidth(NEEDLE_SIZE);

        /*
         * Bilden ska inte fånga musklick.
         * Klicket ska hanteras av Destination-komponenten.
         */
        image.setMouseTransparent(true);

        /*
         * markerBox innehåller nålbilden.
         * Det är denna lilla box som får röd ram när noden markeras.
         */
        markerBox = new StackPane(image);
        markerBox.setPrefSize(MARKER_BOX_SIZE, MARKER_BOX_SIZE);
        markerBox.setMinSize(MARKER_BOX_SIZE, MARKER_BOX_SIZE);
        markerBox.setMaxSize(MARKER_BOX_SIZE, MARKER_BOX_SIZE);
        markerBox.setAlignment(Pos.CENTER);

        /*
         * Placerar markerBox centrerad horisontellt i hela Destination.
         */
        markerBox.setLayoutX(NEEDLE_CENTER_X - MARKER_BOX_SIZE / 2);
        markerBox.setLayoutY(0);

        /*
         * markerBox ska inte blockera klick.
         */
        markerBox.setMouseTransparent(true);

        /*
         * Labeln visar stadens namn under nålen.
         */
        Label nameLabel = new Label(name);
        nameLabel.setPrefWidth(NODE_WIDTH);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setLayoutX(0);
        nameLabel.setLayoutY(MARKER_BOX_SIZE + 1);
        nameLabel.setMouseTransparent(true);
        nameLabel.setStyle(
                "-fx-font-size: 11px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: black; " +
                        "-fx-background-color: white;");

        /*
         * Destination består av två delar:
         * 1. markerBox med nålen
         * 2. nameLabel med stadens namn
         */
        getChildren().addAll(markerBox, nameLabel);

        setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        setMinSize(NODE_WIDTH, NODE_HEIGHT);
        setMaxSize(NODE_WIDTH, NODE_HEIGHT);

        /*
         * Gör att noden kan klickas även om man klickar inom dess tomma yta.
         */
        setPickOnBounds(true);

        /*
         * Behövs för att noden ska kunna flyttas med piltangenter.
         */
        setFocusTraversable(true);

        /*
         * Kopplar mus- och tangentbordshändelser till noden.
         */
        setOnMousePressed(new StartDragHandler());
        setOnMouseDragged(new DragHandler());
        setOnKeyPressed(new KeyHandler());
    }

    public String getName() {
        return name;
    }

    /*
     * Returnerar nodens logiska koordinat.
     * Denna punkt motsvarar nålens centrum.
     */
    public double getX() {
        return newX;
    }

    public double getY() {
        return newY;
    }

    /*
     * Dessa används när App.java ritar kantlinjer.
     *
     * Linjen ska kopplas till nålens centrum.
     */
    public double getConnectionX() {
        return getLayoutX() + NEEDLE_CENTER_X;
    }

    public double getConnectionY() {
        return getLayoutY() + NEEDLE_CENTER_Y;
    }

    /*
     * Används av App.java när en nod markeras eller avmarkeras.
     *
     * Bara markerBox får röd ram.
     * Därför blir markeringsrutan liten och hamnar runt själva nålen.
     */
    public void setSelected(boolean selected) {
        if (selected) {
            markerBox.setStyle(
                    "-fx-border-color: red; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 4; " +
                            "-fx-padding: 1;");
        } else {
            markerBox.setStyle("");
        }
    }

    /*
     * App.java använder denna för att sätta kod som ska köras
     * när Destination flyttas.
     */
    public void setOnPositionChanged(Runnable positionChanged) {
        this.positionChanged = positionChanged;
    }

    private void notifyPositionChanged() {
        if (positionChanged != null) {
            positionChanged.run();
        }
    }

    /*
     * Körs när användaren trycker ner musen på noden.
     */
    class StartDragHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            startX = event.getX();
            startY = event.getY();

            /*
             * Ger noden fokus så att piltangenter fungerar.
             */
            requestFocus();
        }
    }

    /*
     * Körs när användaren drar noden med musen.
     */
    class DragHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            /*
             * Räknar ut ny position för nålens centrum.
             */
            newX = getLayoutX() + event.getX() - startX + NEEDLE_CENTER_X;
            newY = getLayoutY() + event.getY() - startY + NEEDLE_CENTER_Y;

            setCursor(Cursor.CLOSED_HAND);

            /*
             * Flyttar hela Destination så att nålens centrum hamnar på newX/newY.
             */
            relocate(newX - NEEDLE_CENTER_X, newY - NEEDLE_CENTER_Y);

            /*
             * Meddelar App.java att noden har flyttats.
             * Då kan kantlinjerna ritas om.
             */
            notifyPositionChanged();
        }
    }

    class KeyHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case DOWN:
                    newY += 1;
                    break;
                case UP:
                    newY -= 1;
                    break;
                case RIGHT:
                    newX += 1;
                    break;
                case LEFT:
                    newX -= 1;
                    break;
                default:
                    return;
            }

            relocate(newX - NEEDLE_CENTER_X, newY - NEEDLE_CENTER_Y);

            /*
             * Kanter ska följa med även när noden flyttas med piltangenter.
             */
            notifyPositionChanged();

            /*
             * Stoppar tangenttrycket från att skickas vidare.
             */
            event.consume();
        }
    }
}