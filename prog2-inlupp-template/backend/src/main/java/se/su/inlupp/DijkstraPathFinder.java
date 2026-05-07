package se.su.inlupp;

import java.util.*;

public class DijkstraPathFinder<T> implements PathFinder<T> {

    public Path<T> findPath(Graph<T> graph, T start, T end) {
        Map<T, Integer> shortestDistance = new HashMap<>();
        Map<T, T> previousNode = new HashMap<>();

        for (T node : graph.getNodes()) {
            shortestDistance.put(node, Integer.MAX_VALUE);
            previousNode.put(node, null);
        }
        shortestDistance.put(start, 0);

        PriorityQueue<SortableEntrylikeObject> queue = new PriorityQueue<>();
        queue.add(new SortableEntrylikeObject(0, start));
        Set<T> visitedNodes = new HashSet<>();

        T currentNode = start;
        while (!queue.isEmpty() && !currentNode.equals(end)) {
            currentNode = queue.poll().getNode();
            if (visitedNodes.contains(currentNode)) continue;
    
            visitedNodes.add(currentNode);
            for (Edge<T> edge : graph.getEdgesFrom(currentNode)) {
                T nextNode = edge.getDestination();
                int totalWeight = shortestDistance.get(currentNode) + edge.getWeight();
                if (totalWeight < shortestDistance.get(nextNode)) {
                    shortestDistance.put(nextNode, totalWeight);
                    previousNode.put(nextNode, currentNode);
                    queue.add(new SortableEntrylikeObject(shortestDistance.get(nextNode), nextNode));
                }
            }
        }

        if (previousNode.get(end) == null) return null;

        List<T> nodes = new ArrayList<>();
        List<Edge<T>> edges = new ArrayList<>();
        nodes.add(end);
        T current = end;
        while (!current.equals(start) && current != null) {
            T previous = previousNode.get(current);
            nodes.add(previous);
            edges.add(graph.getEdgeBetween(current, previous));
            current = previous;
        }
        Collections.reverse(nodes);
        Collections.reverse(edges);

        return new PathClass<T>(start, end, edges, nodes);
    }

    private class SortableEntrylikeObject implements Comparable<SortableEntrylikeObject> {
        
        private int weight;
        private T node;

        public SortableEntrylikeObject(Integer weight, T node) {
            this.weight = weight;
            this.node = node;
        }

        public T getNode() {
            return node;
        }

        @Override
        public int compareTo(SortableEntrylikeObject p) {
            return weight - p.weight;
        }
    }
}