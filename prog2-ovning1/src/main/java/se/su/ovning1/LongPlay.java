package se.su.ovning1;

public class LongPlay extends Recording {

    public LongPlay(String name, String artist, int year, int condition,double price){
        super(name, artist, year, condition, price);
    }

    public String getType(){
        return "LP";
        
    }

    public String toString(){
        return "LP { name='" + getName() + "', artist='" + getArtist() + "', year=" + getYear() + ", condition=" + getCondition() + ", original price=" + getOriginalPrice() + ", price=" + getPrice() + ", price+VAT=" + getPriceWithVAT() + " }";
    }

    public double getPrice(){
        double value = getOriginalPrice() * getCondition() / 10.0 + (2025 - getYear()) * 5.0;

        if(value < 10){
            return 10;
        } 

        return value;
    }
    
}
