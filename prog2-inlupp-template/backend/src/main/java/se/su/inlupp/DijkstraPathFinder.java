package se.su.inlupp;

import java.util.*;

public class DijkstraPathFinder<T> implements PathFinder<T> {
    public static void main(String[] args) {
        /*ListGraph<City> swedenGraph = new ListGraph<>();
        DijkstraPathFinder<City> pathFinder = new DijkstraPathFinder<>();

        // 1. Skapa städer
        City stockholm = new City("Stockholm");
        City uppsala = new City("Uppsala");
        City enköping = new City("Enköping");
        City västerås = new City("Västerås");
        City örebro = new City("Örebro");

        swedenGraph.add(stockholm);
        swedenGraph.add(uppsala);
        swedenGraph.add(enköping);
        swedenGraph.add(västerås);
        swedenGraph.add(örebro);

        // 2. Koppla samman städerna (Samma logik: Omvägen är billigare)
        swedenGraph.connect(stockholm, örebro, "E18 Direkt", 100);
        
        swedenGraph.connect(stockholm, uppsala, "E4", 10);
        swedenGraph.connect(uppsala, enköping, "Väg 55", 15);
        swedenGraph.connect(enköping, västerås, "E18", 10);
        swedenGraph.connect(västerås, örebro, "E18", 20);

        // 3. Anropa findPath
        System.out.println("Söker kortaste vägen med Dijkstra...");

        Path<City> pathResult = pathFinder.findPath(swedenGraph, stockholm, örebro);

        // 4. Validera och beräkna vikt manuellt
        if (pathResult != null) {
            List<Edge<City>> edges = pathResult.getEdges(); 
            int totalWeight = 0;
            
            System.out.println("Väg hittad från " + stockholm + " till " + örebro + ":");
            for (Edge<City> edge : edges) {
                System.out.println("- " + edge.getName() + " -> " + edge.getDestination() + " (" + edge.getWeight() + ")");
                totalWeight += edge.getWeight(); // Summera vikten här istället
            }
            
            System.out.println("Total beräknad vikt: " + totalWeight);
            
            if (totalWeight == 55) {
                System.out.println("RESULTAT: SUCCESS! Dijkstra valde den billigaste vägen (vikt 55).");
            } else if (totalWeight == 100) {
                System.out.println("RESULTAT: FAIL! Algoritmen tog direktvägen (vikt 100). Kolla din logik!");
            } else {
                System.out.println("RESULTAT: FAIL! Oväntad vikt: " + totalWeight);
            }
        } else {
            System.out.println("RESULTAT: FAIL! Ingen väg hittades.");
        }*/
       // --- NYTT TESTFALL: Norrlandsresan ---
        ListGraph<City> northGraph = new ListGraph<>();
        DijkstraPathFinder<City> finder = new DijkstraPathFinder<>();

        // 1. Skapa städer
        City gavle = new City("Gävle");
        City sundsvall = new City("Sundsvall");
        City lulea = new City("Luleå");
        City ostersund = new City("Östersund");
        City umea = new City("Umeå");
        City kiruna = new City("Kiruna"); // Blindgränd

        northGraph.add(gavle);
        northGraph.add(sundsvall);
        northGraph.add(lulea);
        northGraph.add(ostersund);
        northGraph.add(umea);
        northGraph.add(kiruna);

        // 2. Koppla städerna
        // Väg 1: E4 (Snabbast i antal hopp, men högre vikt)
        northGraph.connect(gavle, sundsvall, "E4 Del 1", 40);
        northGraph.connect(sundsvall, lulea, "E4 Del 2", 50); 
        // Totalt Gävle -> Luleå via E4: 90

        // Väg 2: Inlandsvägen (Fler hopp, men lägre vikt)
        northGraph.connect(gavle, ostersund, "Väg 83", 20);
        northGraph.connect(ostersund, umea, "Väg 363", 30);
        northGraph.connect(umea, lulea, "E4 Norr", 20);
        // Totalt Gävle -> Luleå via Inlandet: 20 + 30 + 20 = 70

        // Väg 3: Blindgränd
        northGraph.connect(ostersund, kiruna, "Malmbanan", 100);

        // 3. Kör sökningen
        System.out.println("\n--- Test 2: Gävle till Luleå ---");
        Path<City> northPath = finder.findPath(northGraph, gavle, lulea);

        if (northPath != null) {
            List<Edge<City>> edges = northPath.getEdges();
            int weight = 0;
            for (Edge<City> e : edges) {
                System.out.println("Från nod till " + e.getDestination() + " via " + e.getName() + " (" + e.getWeight() + ")");
                weight += e.getWeight();
            }
            System.out.println("Total vikt: " + weight);

            if (weight == 70) {
                System.out.println("SUCCESS: Dijkstra hittade den billigaste vägen (70) trots fler hopp!");
            } else {
                System.out.println("FAIL: Dijkstra valde fel väg. Vikt: " + weight);
            }
        } else {
            System.out.println("FAIL: Ingen väg hittades!");
        }
    }

    public Path<T> findPath(Graph<T> graph, T start, T end) {
        Map<T, Integer> shortestDistance = new HashMap<>();
        Map<T, T> previousNode = new HashMap<>();

        // Fyll tabellerna för distanser mellan startnod och alla andra noder med
        // utgångsvärden
        // samt alla noder i tabellen previous och sätter prev för alla till null
        for (T node : graph.getNodes()) {
            shortestDistance.put(node, Integer.MAX_VALUE);
            previousNode.put(node, null);
        }
        shortestDistance.put(start, 0); // gör distansen till startnoden 0

        // Skapa prioritetskö & sätt in första noden med distans 0 & skapa bokföring av
        // besökta noder
        PriorityQueue<Pair> queue = new PriorityQueue<>();
        queue.add(new Pair(0, start));
        Set<T> visitedNodes = new HashSet<>();

        // Själva algoritmen
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
                    queue.add(new Pair(shortestDistance.get(nextNode), nextNode));
                }
            }
        }

        if (shortestDistance.get(end) == Integer.MAX_VALUE) {
            return null;
        }

        // Skapa listor av noder och kanter för ett Path-objekt
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

    private class Pair implements Comparable<Pair> {
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
        public int compareTo(Pair p) {
            return weight - p.weight;
        }

        public boolean equals(Object o) {
            if (o instanceof DijkstraPathFinder.Pair p) {
                return Objects.equals(node, p.getNode());
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(node);
        }
    }
}