package se.su.inlupp;

import java.util.*;

public class BFSPathFinder<T> implements PathFinder<T> {

  @Override
  public Path<T> findPath(Graph<T> graph, T start, T end) {
    try {
      Map<T, T> connected = new HashMap<>();
      connected.put(start, null);

      LinkedList<T> queue = new LinkedList<>();
      queue.add(start);

      while (!queue.isEmpty() && !connected.containsKey(end)) {
        T current = queue.poll();
        for (Edge<T> edge : graph.getEdgesFrom(current)) {
          T next = edge.getDestination();
          if (!connected.containsKey(next)) {
            connected.put(next, current);
            queue.add(next);
          }
        }
      }

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
    
      return new PathClass<T>(start, end, edges, nodes);
    } catch (NoSuchElementException e) { return null; }
  }
}