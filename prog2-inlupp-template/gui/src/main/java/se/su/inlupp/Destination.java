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

public class Destination extends Pane {

    /*
     * Bredden på hela den grafiska noden.
     * Noden består inte bara av nålen, utan också av stadens namn.
     * Därför behöver den vara bredare än själva nålbilden.
     */
    private static final double NODE_WIDTH = 80;

    /*
     * Höjden på hela den grafiska noden.
     *
     * Höjden ska rymma både:
     * - nålen
     * - stadens namn under nålen
     */
    private static final double NODE_HEIGHT = 45;

    // Storleken på själva nålbilden.

    private static final double NEEDLE_SIZE = 20;

    /*
     * Nålens centrum i sidled inuti hela Destination.
     * Detta används för att:
     * - centrera nålen
     * - placera noden rätt när användaren klickar på kartan
     * - rita kanter till nålens centrum
     */
    private static final double NEEDLE_CENTER_X = NODE_WIDTH / 2;

    /*
     * Nålens centrum i höjdled.
     * Detta används när kanter ska kopplas till nålen och när noden flyttas.
     */
    private static final double NEEDLE_CENTER_Y = NEEDLE_SIZE / 2 + 12;

    /*
     * startX och startY sparar var på noden användaren klickade
     * när dragningen startade.
     *
     * Det gör att noden inte "hoppar" när man börjar dra den.
     */
    private double startX;
    private double startY;

    /*
     * newX och newY är nodens logiska position på kartan.
     *
     * newX/newY är själva positionen för nålens centrum.
     *
     * Det är dessa koordinater som sparas i City.
     */
    private double newX;
    private double newY;

    /*
     * Stadens namn som visas under nålen.
     */
    private String name;

    /*
     * positionChanged är en callback.
     *
     * App.java skickar in kod hit med setOnPositionChanged(...).
     * Den koden ska köras varje gång Destination flyttas.
     *
     * Det behövs eftersom Destination bara vet hur den flyttas grafiskt.
     * App.java måste sedan uppdatera City-koordinaterna och rita om kanterna.
     */
    private Runnable positionChanged;

    public Destination(String name, double x, double y) {
        this.name = name;
        newX = x;
        newY = y;

        /*
         * relocate placerar hela Destination-komponentens övre vänstra hörn.
         *
         * Men vi vill att nålens centrum ska hamna på newX/newY.
         * Därför flyttar vi hela Destination lite åt vänster och uppåt.
         */
        relocate(newX - NEEDLE_CENTER_X, newY - NEEDLE_CENTER_Y);

        ImageView image = new ImageView(new Image(Destination.class.getResourceAsStream("/nål.png")));

        image.setFitHeight(NEEDLE_SIZE);
        image.setFitWidth(NEEDLE_SIZE);

        // Placerar nålbilden centrerad i Destination.

        image.setLayoutX(NEEDLE_CENTER_X - NEEDLE_SIZE / 2);
        image.setLayoutY(0);

        /*
         * Bilden ska inte fånga musklick.
         *
         * Klick ska gå till hela Destination, annars kan markering och dragning
         * bli svårare att hantera.
         */
        image.setMouseTransparent(true);

        /*
         * Skapar texten som visar stadens namn under nålen.
         */
        Label nameLabel = new Label(name);

        /*
         * Labeln får samma bredd som hela Destination.
         *
         * Det gör att texten kan centreras under nålen.
         */
        nameLabel.setPrefWidth(NODE_WIDTH);
        nameLabel.setAlignment(Pos.CENTER);

        /*
         * Labeln börjar längst till vänster i Destination.
         */
        nameLabel.setLayoutX(0);

        // Labeln placeras precis under nålen.

        nameLabel.setLayoutY(NEEDLE_SIZE + 1);

        /*
         * Labeln ska inte fånga klick.
         *
         * Om användaren klickar på stadens namn ska klicket ändå hanteras
         * av hela Destination.
         */
        nameLabel.setMouseTransparent(true);

        nameLabel.setStyle(
                "-fx-font-size: 11px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: black; " +
                        "-fx-background-color: white;");

        /*
         * Lägger in nålbilden och namnet i Destination.
         *
         * Utan denna rad skulle varken nålen eller namnet synas.
         */
        getChildren().addAll(image, nameLabel);

        /*
         * Låser storleken på hela Destination.
         * Genom att sätta alla tre blir nodens storlek stabil.
         */
        setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        setMinSize(NODE_WIDTH, NODE_HEIGHT);
        setMaxSize(NODE_WIDTH, NODE_HEIGHT);

        /*
         * Gör att hela Destination-rutan kan fånga klick,
         * även tom yta inom nodens osynliga rektangel.
         */
        setPickOnBounds(true);

        /*
         * Gör att Destination kan få tangentbordsfokus.
         *
         * Detta behövs för att piltangenter ska kunna flytta noden.
         */
        setFocusTraversable(true);

        // Kopplar mus- och tangenthantering till noden.

        setOnMousePressed(new StartDragHandler());
        setOnMouseDragged(new DragHandler());
        setOnKeyPressed(new KeyHandler());
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

    public double getConnectionX() {
        return getLayoutX() + NEEDLE_CENTER_X;
    }

    public double getConnectionY() {
        return getLayoutY() + NEEDLE_CENTER_Y;
    }

    // Markerar eller avmarkerar noden.

    public void setSelected(boolean selected) {
        if (selected) {
            setStyle(
                    "-fx-border-color: red; " +
                            "-fx-border-width: 3; " +
                            "-fx-border-radius: 4;");
        } else {
            setStyle("");
        }
    }

    /*
     * App.java använder denna metod för att skicka in kod som ska köras
     * varje gång Destination flyttas.
     *
     * Det är så Destination kan meddela App.java:
     * "Jag har flyttats".
     */
    public void setOnPositionChanged(Runnable positionChanged) {
        this.positionChanged = positionChanged;
    }

    /*
     * Kör callbacken om en sådan finns.
     *
     * Den används efter att noden har flyttats, så att App.java kan:
     * - uppdatera City-koordinater
     * - rita om kanter
     * - markera osparade ändringar
     */
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
            /*
             * Sparar var i noden användaren klickade.
             *
             * Detta används senare i DragHandler för att noden inte ska hoppa.
             */
            startX = event.getX();
            startY = event.getY();

            /*
             * Ger noden fokus så att piltangenter kan användas.
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
             *
             * getLayoutX/getLayoutY är Destinationens övre vänstra hörn.
             * event.getX/getY är musens position inuti Destination.
             * startX/startY är där dragningen började.
             *
             * NEEDLE_CENTER_X/Y läggs till eftersom newX/newY ska betyda
             * nålens centrum, inte Destinationens övre vänstra hörn.
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

            /*
             * Flyttar noden visuellt efter tangenttrycket.
             */
            relocate(newX - NEEDLE_CENTER_X, newY - NEEDLE_CENTER_Y);

            /*
             * Meddelar App.java så att kanterna följer med.
             */
            notifyPositionChanged();

            event.consume();
        }
    }
}