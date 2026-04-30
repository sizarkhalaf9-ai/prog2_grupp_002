package se.su.inlupp;

import java.util.*;

public class DijkstraPathFinder<T> implements PathFinder<T> {
    public static void main(String[] args) {
        
    }

    public Path<T> findPath(Graph<T> graph, T start, T end) {
        Map<T, Integer> shortestDistance = new HashMap<>();
        Map<T, T> previousNode = new HashMap<>();

        //Fyll tabellerna för distanser mellan startnod och alla andra noder med utgångsvärden
        //samt alla noder i tabellen previous och sätter prev för alla till null
        for (T node : graph.getNodes()) {
            shortestDistance.put(node, Integer.MAX_VALUE);
            previousNode.put(node, null);
        }
        shortestDistance.put(start, 0); //gör distansen till startnoden 0

        //Skapa prioritetskö & sätt in första noden med distans 0 & skapa bokföring av besökta noder
        PriorityQueue<Pair<T>> queue = new PriorityQueue<>();
        queue.add(new Pair<T>(0, start));
        Set<T> visitedNodes = new HashSet<>();

        //Själva algoritmen
        while (!queue.isEmpty()) {
            T currentNode = queue.poll().getNode();
            if (currentNode.equals(end)) {
                break;
            }
            if (visitedNodes.contains(currentNode)) {
                continue;
            }
            visitedNodes.add(currentNode);
            for (Edge<T> edge : graph.getEdgesFrom(currentNode)) {
                T nextNode = edge.getDestination();
                int totalWeight = shortestDistance.get(currentNode) + edge.getWeight();
                if (totalWeight < shortestDistance.get(nextNode)) {
                    shortestDistance.put(nextNode, totalWeight);
                    previousNode.put(nextNode, currentNode);
                    queue.add(new Pair<T>(shortestDistance.get(nextNode), nextNode));
                }
            }
        }
        
        if (shortestDistance.get(end) == Integer.MAX_VALUE) {
            return null;
        }

        //Skapa listor av noder och kanter för ett Path-objekt
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

    private class Pair<T> implements Comparable<Pair<T>> {
        private int weight;
        private T node;

        public Pair(Integer weight, T node) {
            this.weight = weight;
            this.node = node;
        }

        public T getNode() {
            return node;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        @Override
        public int compareTo(Pair<T> p) {
            return weight - p.weight;
        }

        public boolean equals(Object o) {
            if (o instanceof DijkstraPathFinder<T>.Pair<T> p) {
                return Objects.equals(node, p.getNode());
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(node);
        }
    }
}