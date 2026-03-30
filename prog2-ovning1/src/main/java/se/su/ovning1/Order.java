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
        String receipt = "";




        
    }
    // Vi behöver formatera getReceipt-metoden.
}
Receipt for order #2
-----------
Book { name='Beethoven: a biography', author='Holmqvist', bound=false, price=400.0, price+VAT=424.0 }
LP { name='Giant Steps', artist='John Coltrane', year=1959, condition=10, original price=100.0, price=425.0, price+VAT=531.25 }
CD { name='Kind of Blue', artist='Miles Davis', year=1959, condition=5, original price=100.0, price=50.0, price+VAT=62.5 }
Total excl. VAT: 875.0
Total incl. VAT: 1017.75
-----------