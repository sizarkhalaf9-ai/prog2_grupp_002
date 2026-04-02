package se.su.ovning1;

public class Book extends Item implements PriceableWithVAT6 {
    private final boolean bound;
    private final double price;
    private final String author;

    public Book(String name, String author, double price, boolean bound) {
        super(name);
        this.author = author;
        this.price = price;
        this.bound = bound;
    }

    /* public String getAuthor(){
        return author;
    }
    
   public boolean getBound(){
        return bound;
        }  */

    public double getPrice() {
        if (bound) {
            return price * 1.3;
        } else {
            return price;
        }
    }

   /*  public String getType(){
        return "Book";
        } */

    public String toString() {
        return getName() + ", " + author + ", " + getPrice() + ", " + bound;
    }
}

