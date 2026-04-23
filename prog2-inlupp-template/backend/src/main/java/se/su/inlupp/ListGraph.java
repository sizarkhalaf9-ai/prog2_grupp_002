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
    if (!graph.containsKey(node1) || !graph.containsKey(node2)) {
      throw new NoSuchElementException();
    }
    if (weight < 0) {
      throw new IllegalArgumentException();
    }
    this.add(node1);
    this.add(node2);

    Edge<T> edge1 = new EdgeClass<>(node2, name, weight);
    Edge<T> edge2 = new EdgeClass<>(node1, name, weight);

    if (getEdgeBetween(node1, node2) == null) {
      graph.get(node1).add(edge1);
      graph.get(node2).add(edge2);
    } else {
      throw new IllegalStateException();
    }

  }

  @Override
  public void disconnect(T node1, T node2) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2))
      throw new NoSuchElementException();

    Edge<T> edge1 = getEdgeBetween(node1, node2);
    Edge<T> edge2 = getEdgeBetween(node2, node1);

    if (edge1 == null || edge2 == null) {
      throw new IllegalStateException();
    } else {
      graph.get(node1).remove(edge1);
      graph.get(node2).remove(edge2);
    }
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    if (!graph.containsKey(node1) || !graph.containsKey(node2) || getEdgeBetween(node1, node2) == null
        || getEdgeBetween(node2, node1) == null)
      throw new NoSuchElementException();
    if (weight < 0)
      throw new IllegalArgumentException();

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

      for (Edge<T> edge : getEdgesFrom(node)) {
        sb.append(edge.toString() + ", ");
      }
    }
    return sb.toString();
  }

  /*
   * Letade efter följande ord:
   * [A, B, till B med G -] B tar 28, C, D, E, till D med C -] D tar 5, F, G, H,
   * I, J, till B med B -] D tar 2, till I med D -] I tar 1, till C med E -] C tar
   * 2, till J med J -] D tar 5, till I med H -] I tar 3, till D med D -] I tar 1,
   * till B med B -] F tar 5, till E med E -] D tar 2, till H med F -] H tar 1, X,
   * till H med H -] D tar 1, till C med C -] D tar 5, till C med B -] C tar 3,
   * till G med A -] G tar 3, till H med H -] I tar 3, till B med B -] C tar 3,
   * till D med H -] D tar 1, till D med E -] D tar 2, till F med B -] F tar 5,
   * till E med E -] C tar 2, till F med F -] H tar 1, till G med G -] B tar 28,
   * till D med B -] D tar 2, till A med A -] G tar 3, till D med J -] D tar 5] i
   * strängen:
   * 
   * till G med A -] G tar 3, till F med B -] F tar 5, till C med B -] C tar 3,
   * till D med B -] D tar 2, till G med G -] B tar 28, till B med B -] C tar 3,
   * till D med C -] D tar 5, till E med E -] C tar 2, till J med J -] D tar 5,
   * till H med H -] D tar 1, till B med B -] D tar 2, till C med C -] D tar 5,
   * till I med D -] I tar 1, till E med E -] D tar 2, till C med E -] C tar 2,
   * till D med E -] D tar 2, till H med F -] H tar 1, till B med B -] F tar 5,
   * till B med G -] B tar 28, till A med A -] G tar 3, till F med F -] H tar 1,
   * till D med H -] D tar 1, till I med H -] I tar 3, till D med D -] I tar 1,
   * till H med H -] I tar 3, till D med J -] D tar 5,
   * men något saknades.
   * ==] expected: [true] but was: [false
   */
  // "till G med A -> G tar 3"
  @Override
  public Iterator<T> iterator() {
    return graph.keySet().iterator();
  }

}
