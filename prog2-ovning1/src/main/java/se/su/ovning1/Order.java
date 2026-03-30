package se.su.ovning1;

import java.util.List;
import java.util.ArrayList;
//HEJ
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
        String receipt = "";

    }
    // Vi behöver formatera getReceipt-metoden.
}
