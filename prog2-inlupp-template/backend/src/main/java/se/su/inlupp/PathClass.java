package se.su.inlupp;

import java.util.Iterator;
import java.util.List;

public class PathClass<T> implements Path<T> {
    private T start;
    private T end;
    private int totalWeight;
    private List<Edge<T>> edges;
    private List<T> nodes;

    public PathClass(T start, T end, List<Edge<T>> edges, List<T> nodes) {
        this.start = start;
        this.end = end;
        this.edges = edges;
        this.nodes = nodes;
    }

    @Override
    public T getStart() {
        return start;
    }

    @Override
    public T getEnd() {
        return end;
    }

    @Override
    public int getTotalWeight() {
        int totalWeight = 0;
        for (Edge<T> edge : edges) {
            totalWeight += edge.getWeight();
        }
        return this.totalWeight = totalWeight;
    }

    @Override
    public List<Edge<T>> getEdges() {
        return edges;
    }

    @Override
    public List<T> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return start + "-" + end + ", total stops: " + edges.size() + ". Total distance: " + totalWeight;
    }

    @Override
    public Iterator<Edge<T>> iterator() {
        return edges.iterator();
    }
}
