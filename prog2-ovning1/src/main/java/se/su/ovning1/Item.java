package se.su.ovning1;

public class Item Implements Priceable {
    private String name;

    protected Item(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }
}
