package se.su.ovning1;

public abstract class Recording extends Item implements PriceableWithVAT25{

    private String artist;
    private int year;
    private int condition;
    private double price;

    protected Recording(String name, String artist, int year, int condition, double price){
        super(name);
        this.artist = artist;
        this.year = year;
        this.condition = condition;
        this.price = price;
    }

    public String getArtist(){
        return artist;
    }

    public int getYear(){
        return year;
    }

    public int getCondition(){
        return condition;
    }

    protected double getOriginalPrice(){
        return originalPrice;
    }

    public double getPrice(){
        double value = originalPrice * (condition/10.0);

        
    }

    public abstract String getType();

    // Kola på originalPrice-delen! vi måste fixa den på något sätt!
    

}
