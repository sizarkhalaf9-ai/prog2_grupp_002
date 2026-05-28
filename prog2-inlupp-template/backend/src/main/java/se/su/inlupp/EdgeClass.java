// PROG2 VT2026, Inlämningsuppgift, del 1 
// Grupp 002 
// Julia Liu juli0873
// Mashfi Saiyad masa1911
// Sizar Khalaf sikh1472

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

    @Override
    public String toString() {
        return "till " + destination + " med " + name + " tar " + weight;
    }
}
