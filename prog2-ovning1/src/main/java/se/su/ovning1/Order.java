package se.su.ovning1;

import java.util.List;
import java.util.ArrayList;

public class Order {

    private final long orderNumber;
    private static long counter = 0;
    private final List<Item> items;

    public Order(Item... items) {
        this.items = new ArrayList<>();

        for (Item i : items) {
            this.items.add(i);
        }
        this.orderNumber = counter++;
    }

    public double getTotalValue() {
        double sum = 0;
        for (Item i : items) {
            sum += i.getPrice();
        }
        return sum;
    }

    public double getTotalValuePlusVAT() {
        double sum = 0;
        for (Item i : items) {
            sum += i.getPriceWithVAT();
        }
        return sum;
    }

    public String getReceipt() {
        String kvittoDel1 = "\nReceipt for order #" + counter + "\n-----------\n";
        String kvittoDel2 = "";
        for (Item item : items) {
            if (item instanceof Book) {
                String[] parsad = ((Book) item).toString().split(",");
                kvittoDel2 += "Book { name='" + parsad[0] + "', author='" + parsad[1] + "', bound=" + parsad[2] + ",\nprice=" + parsad[3] + ", price+VAT=" + item.getPriceWithVAT() + " }\n";
                //kvittoDel2 += "Book { name='" + ((Book) item).toString();
            } else if (item instanceof CompactDisc) {
                CompactDisc CD = (CompactDisc) item;
                kvittoDel2 += CD.getType() +  " { name='" + CD.getName() + "', artist='" + CD.getArtist() + "', year=" + CD.getYear() + ", condition=" + CD.getCondition() + "\noriginal price=" + CD.getOriginalPrice() + ", price=" + CD.getPrice() + ", price+VAT=" + CD.getPriceWithVAT() + " }\n";
            } else if (item instanceof LongPlay) {
                LongPlay LP = (LongPlay) item;
                kvittoDel2 += LP.getType() +  " { name='" + LP.getName() + "', artist='" + LP.getArtist() + "', year=" + LP.getYear() + ", condition=" + LP.getCondition() + "\noriginal price=" + LP.getOriginalPrice() + ", price=" + LP.getPrice() + ", price+VAT=" + LP.getPriceWithVAT() + " }\n";
            }
        }
        String kvittoDel3 = "Total excl. VAT: " + getTotalValue() + "\nTotal incl. VAT: " + getTotalValuePlusVAT() + "\n-----------\n";
        return kvittoDel1 + kvittoDel2 + kvittoDel3;
    }
}