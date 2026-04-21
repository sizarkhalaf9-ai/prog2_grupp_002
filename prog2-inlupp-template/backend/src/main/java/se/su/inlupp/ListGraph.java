package se.su.inlupp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ListGraph<T> implements Graph<T> {

  private final Map<T, Set<Edge<T>>> graph = new HashMap<>();

  @Override
  public void add(T node) {
    graph.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void remove(T node) {
    if (!graph.containsKey(node)) {
      throw new NoSuchElementException();
    } 
    graph.remove(node);
  }

  @Override
  public boolean hasNode(T node) {
    return graph.containsKey(node);
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2)) {
      throw new NoSuchElementException();
    } if (weight < 0) {
      throw new IllegalArgumentException();
    } 
    this.add(node1);
    this.add(node2);

    //Set<Edge<T>> node1edges = graph.get(node1);
    //Set<Edge<T>> node2edges = graph.get(node2);

    Edge<T> node1edge = new EdgeClass<>(node2, name, weight);
    Edge<T> node2edge = new EdgeClass<>(node2, name, weight);

    if (graph.get(node1).contains(node1edge) || graph.get(node2).contains(node2edge)) {
      throw new IllegalStateException();
    } else {
      graph.get(node1).add(node1edge);
      graph.get(node2).add(node2edge);
    }
  }

  @Override
  public void disconnect(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    throw new UnsupportedOperationException("Unimplemented method 'setConnectionWeight'");
  }

  @Override
  public Set<T> getNodes() {
    return graph.keySet();
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    if (!graph.containsKey(node)) {
      throw new NoSuchElementException();
    }
    Collection<Edge<T>> copy = new HashSet<>(graph.get(node));
    return copy;
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'getEdgeBetween'");
  }

  @Override
  public Iterator<T> iterator() {
    throw new UnsupportedOperationException("Unimplemented method 'iterator'");
  }
}

