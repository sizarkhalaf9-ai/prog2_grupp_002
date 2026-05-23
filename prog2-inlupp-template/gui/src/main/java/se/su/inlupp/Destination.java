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
     * ÄNDRING: Dessa konstanter används för att få bättre kontroll över var nålen,
     * namnet och linjerna ska placeras.
     * 
     * NODE_WIDTH är hela komponentens bredd, alltså nål + namn.
     * NEEDLE_SIZE är själva nålbildens storlek.
     * MARKER_BOX_SIZE är den lilla rutan runt nålen när noden är markerad.
     */
    private static final double NODE_WIDTH = 80;
    private static final double NODE_HEIGHT = 45;
    private static final double NEEDLE_SIZE = 20;
    private static final double MARKER_BOX_SIZE = 24;

    /*
     * ÄNDRING:
     * Dessa används för att veta exakt var mitten av nålen är.
     * Linjer mellan noder ska kopplas till mitten av nålen, inte till texten under
     * noden.
     */
    private static final double NEEDLE_CENTER_X = NODE_WIDTH / 2;
    private static final double NEEDLE_CENTER_Y = NEEDLE_SIZE / 2;

    private double startX;
    private double startY;
    private double newX;
    private double newY;

    private String name;

    /*
     * ÄNDRING:
     * positionChanged är en callback.
     * App.java kan sätta denna så att App får veta när en nod flyttas.
     * Då kan App.java rita om alla kantlinjer.
     */
    private Runnable positionChanged;

    /*
     * ÄNDRING:
     * markerBox är bara den lilla rutan runt nålen.
     * Tidigare markerades hela Destination, alltså både nål och namn.
     * Då blev den röda markeringsrutan för stor.
     */
    private StackPane markerBox;

    public Destination(String name, double x, double y) {
        this.name = name;
        newX = x;
        newY = y;

        /*
         * ÄNDRING:
         * Vi placerar hela Destination så att nålens mittpunkt hamnar på newX/newY.
         * Därför flyttar vi komponenten med -NEEDLE_CENTER_X och -NEEDLE_CENTER_Y.
         */
        relocate(newX - NEEDLE_CENTER_X, newY - NEEDLE_CENTER_Y);

        /*
         * Laddar in nålbilden från resources.
         * Hos er fungerar /nål.png.
         */
        ImageView image = new ImageView(new Image(Destination.class.getResourceAsStream("/nål.png")));
        image.setFitHeight(NEEDLE_SIZE);
        image.setFitWidth(NEEDLE_SIZE);

        /*
         * Gör att själva bilden inte blockerar musklick.
         * Klicket ska fångas av hela Destination-komponenten.
         */
        image.setMouseTransparent(true);

        /*
         * ÄNDRING:
         * Nålbilden läggs i markerBox.
         * Det är markerBox som får röd ram när noden markeras.
         */
        markerBox = new StackPane(image);
        markerBox.setPrefSize(MARKER_BOX_SIZE, MARKER_BOX_SIZE);
        markerBox.setMinSize(MARKER_BOX_SIZE, MARKER_BOX_SIZE);
        markerBox.setMaxSize(MARKER_BOX_SIZE, MARKER_BOX_SIZE);
        markerBox.setAlignment(Pos.CENTER);

        // Placerar markerBox centrerad i Destination.

        markerBox.setLayoutX(NEEDLE_CENTER_X - MARKER_BOX_SIZE / 2);
        markerBox.setLayoutY(0);

        /*
         * markerBox ska inte blockera klick.
         * Hela Destination fångar klicket.
         */
        markerBox.setMouseTransparent(true);

        // ÄNDRING: Stadens namn visas under nålen.

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
                        "-fx-background-color: rgba(255,255,255,0.7);");

        /*
         * Destination består nu av:
         * 1. markerBox med nålen
         * 2. nameLabel med stadens namn
         */
        getChildren().addAll(markerBox, nameLabel);

        setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        setMinSize(NODE_WIDTH, NODE_HEIGHT);
        setMaxSize(NODE_WIDTH, NODE_HEIGHT);

        // Gör Destination klickbar även om man klickar inom dess tomma yta.

        setPickOnBounds(true);

        /*
         * Gör att Destination kan få tangentbordsfokus,
         * så att piltangenter kan flytta noden.
         */
        setFocusTraversable(true);

        setOnMousePressed(new StartDragHandler());
        setOnMouseDragged(new DragHandler());
        setOnKeyPressed(new KeyHandler());
    }

    public String getName() {
        return name;
    }

    /*
     * getX/getY returnerar nodens logiska koordinat,
     * alltså punkten där själva nålen sitter.
     */
    public double getX() {
        return newX;
    }

    public double getY() {
        return newY;
    }

    /*
     * ÄNDRING:
     * Dessa två metoder används av App.java när den ritar kantlinjer.
     * Linjen ska sitta i nålens centrum, inte i texten under noden.
     */
    public double getConnectionX() {
        return getLayoutX() + NEEDLE_CENTER_X;
    }

    public double getConnectionY() {
        return getLayoutY() + NEEDLE_CENTER_Y;
    }

    /*
     * ÄNDRING:
     * App.java använder denna metod för att markera/avmarkera en nod.
     * Bara markerBox får röd ram, inte hela Destination.
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
     * ÄNDRING:
     * App.java sätter denna callback.
     * När Destination flyttas körs callbacken så att App.java kan rita om kanter.
     */
    public void setOnPositionChanged(Runnable positionChanged) {
        this.positionChanged = positionChanged;
    }

    private void notifyPositionChanged() {
        if (positionChanged != null) {
            positionChanged.run();
        }
    }

    class StartDragHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            startX = event.getX();
            startY = event.getY();

            // Ger noden tangentbordsfokus så att piltangenter fungerar.

            requestFocus();
        }
    }

    class DragHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            // Räknar ut den nya logiska positionen för nålens centrum.

            newX = getLayoutX() + event.getX() - startX + NEEDLE_CENTER_X;
            newY = getLayoutY() + event.getY() - startY + NEEDLE_CENTER_Y;

            setCursor(Cursor.CLOSED_HAND);

            // Flyttar hela Destination så att nålens centrum hamnar på newX/newY.

            relocate(newX - NEEDLE_CENTER_X, newY - NEEDLE_CENTER_Y);

            /*
             * ÄNDRING:
             * Meddelar App.java att noden flyttats.
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
             * ÄNDRING:
             * Även när noden flyttas med piltangenter ska kanterna följa med.
             */
            notifyPositionChanged();

            // Stoppar tangenttrycket från att skickas vidare.

            event.consume();
        }
    }
}