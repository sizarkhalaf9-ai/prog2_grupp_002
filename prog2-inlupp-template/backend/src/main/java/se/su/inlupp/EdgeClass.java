package se.su.inlupp;

import java.io.Serializable;
import java.util.Objects;

public class EdgeClass<T> implements Edge<T>, Serializable {
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
    public boolean equals(Object obj) {
        if (obj instanceof EdgeClass edge) {
            return this.weight == edge.weight && this.name.equals(edge.name) && this.destination.equals(edge.destination);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, name, destination);
    }

    public String toString() {
        return destination.toString() + "; " + name + "; " + weight;
    }
}
