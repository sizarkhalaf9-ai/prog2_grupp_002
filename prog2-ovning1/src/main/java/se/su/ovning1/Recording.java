package se.su.ovning1;

public abstract class Recording extends Item implements PriceableWithVAT25 {

    private final String artist;
    private final int year;
    private int condition;
    private final double price;

    protected Recording(String name, String artist, int year, int condition, double price) {
        super(name);
        this.artist = artist;
        this.year = year;
        this.condition = condition;
        this.price = price;
    }

    public String getArtist() {
        return artist;
    }

    public int getYear() {
        return year;
    }

    public int getCondition() {
        return condition;
    }

    protected double getOriginalPrice() {
        return price;
    }

    public double getPrice() {
        double value = price * condition / 10.0;

        return (value < 10) ? 10 : value;
    }

    public abstract String getType();

    public String toString() {

        return getType() + " { name='" + getName() + "', artist='" + artist + "', year=" + year + ", condition=" + condition
                + ", original price=" + price + "price=" + getPrice() + ", price+VAT=" + getPriceWithVAT() + " }";
    }
}
