package se.su.inlupp;

public class EdgeClass<T> implements Edge<T> {
    private T destination;
    private String name;
    private int weight;

    public EdgeClass(T destination, String name, int weight) {
        this.destination = destination;
        this.name = name;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight < 0)
            throw new IllegalArgumentException();
        this.weight = weight;
    }

    public T getDestination() {
        return this.destination;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "till " + destination.toString() + " med " + name + " tar " + weight;
    }

    
}
