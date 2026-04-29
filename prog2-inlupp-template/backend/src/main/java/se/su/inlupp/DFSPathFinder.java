package se.su.inlupp;

import java.util.*;

public class DFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T start, T end) {
    try {
      Map<T, T> connected = new HashMap<>();
      connect(null, start, connected, graph);

      LinkedList<Edge<T>> edges = new LinkedList<>();
      LinkedList<T> nodes = new LinkedList<>();
      T current = end;
      nodes.add(current);
      while (current != null && !current.equals(start)) {
        T previous = connected.get(current);
        edges.addFirst(graph.getEdgeBetween(previous, current));
        nodes.addFirst(previous);
        current = previous;
      }

      Path<T> path = new PathClass<>(start, end, edges, nodes);
      
      return path; 
    } catch (NoSuchElementException e) { return null; }
  }
  
  private void connect(T step0, T step1, Map<T, T> connected, Graph<T> graph) {
    connected.put(step1, step0);
    for (Edge<T> edge : graph.getEdgesFrom(step1)) {
      T step2 = edge.getDestination();
      if (!connected.containsKey(step2))
        connect(step1, step2, connected, graph);
    }
  }
}