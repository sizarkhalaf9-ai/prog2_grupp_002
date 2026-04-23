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
    if (!graph.containsKey(node))
      throw new NoSuchElementException();

    Set<Edge<T>> edges = new HashSet<>(graph.get(node));
    for (Edge<T> edge : edges) {
      T destination = edge.getDestination();
      disconnect(node, destination);
    }
    graph.remove(node);
  }

  @Override
  public boolean hasNode(T node) {
    
    return graph.containsKey(node);
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2))
      throw new NoSuchElementException();
    if (weight < 0) throw new IllegalArgumentException();

    this.add(node1);
    this.add(node2);

    if (getEdgeBetween(node1, node2) == null) {
      graph.get(node1).add(new EdgeClass<>(node2, name, weight));
      graph.get(node2).add(new EdgeClass<>(node1, name, weight));
    } else throw new IllegalStateException();
  }

  @Override
  public void disconnect(T node1, T node2) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2))
      throw new NoSuchElementException();

    Edge<T> edge1 = getEdgeBetween(node1, node2);
    Edge<T> edge2 = getEdgeBetween(node2, node1);

    if (getEdgeBetween(node1, node2) == null || getEdgeBetween(node2, node1) == null)
      throw new IllegalStateException();
    else {
      graph.get(node1).remove(edge1);
      graph.get(node2).remove(edge2);
    }
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2) || 
        getEdgeBetween(node1, node2) == null || getEdgeBetween(node2, node1) == null)
      throw new NoSuchElementException();
    if (weight < 0) throw new IllegalArgumentException();

    getEdgeBetween(node1, node2).setWeight(weight);
    getEdgeBetween(node2, node1).setWeight(weight);
  }

  @Override
  public Set<T> getNodes() {

    return graph.keySet();
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    if (!graph.containsKey(node))
      throw new NoSuchElementException();

    return new HashSet<>(graph.get(node));
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2))
      throw new NoSuchElementException();

    for (Edge<T> edge : graph.get(node1)) {
      if (edge.getDestination().equals(node2))

        return edge;
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Set<T> allNodes = getNodes();
    for (T node : allNodes) {
      sb.append(node.toString() + ", ");
      for (Edge<T> edge : getEdgesFrom(node)) {
        sb.append(edge.toString() + ", ");
      }
    }
    String string = sb.toString();
  
    return string.substring(0, string.length() - 1);
  }

  @Override
  public Iterator<T> iterator() {

    return graph.keySet().iterator();
  }
}