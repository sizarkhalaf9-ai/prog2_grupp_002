package se.su.inlupp;

import java.util.List;

public class PathFinderTest {
    public static void main(String[] args) {
        ListGraph<CityClass> swedenGraph = new ListGraph<>();
        DijkstraPathFinder<CityClass> pathFinder = new DijkstraPathFinder<>();

        // 1. Skapa städer
        CityClass stockholm = new CityClass("Stockholm");
        CityClass uppsala = new CityClass("Uppsala");
        CityClass enköping = new CityClass("Enköping");
        CityClass västerås = new CityClass("Västerås");
        CityClass örebro = new CityClass("Örebro");

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

        Path<CityClass> pathResult = pathFinder.findPath(swedenGraph, stockholm, örebro);

        // 4. Validera och beräkna vikt manuellt
        if (pathResult != null) {
            List<Edge<CityClass>> edges = pathResult.getEdges(); 
            int totalWeight = 0;
            
            System.out.println("Väg hittad från " + stockholm + " till " + örebro + ":");
            for (Edge<CityClass> edge : edges) {
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
        }
       // --- NYTT TESTFALL: Norrlandsresan ---
        ListGraph<CityClass> northGraph = new ListGraph<>();
        DijkstraPathFinder<CityClass> finder = new DijkstraPathFinder<>();

        // 1. Skapa städer
        CityClass gavle = new CityClass("Gävle");
        CityClass sundsvall = new CityClass("Sundsvall");
        CityClass lulea = new CityClass("Luleå");
        CityClass ostersund = new CityClass("Östersund");
        CityClass umea = new CityClass("Umeå");
        CityClass kiruna = new CityClass("Kiruna"); // Blindgränd

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
        Path<CityClass> northPath = finder.findPath(northGraph, gavle, lulea);
        
        if (northPath != null) {
            List<Edge<CityClass>> edges = northPath.getEdges();
            int weight = 0;
            for (Edge<CityClass> e : edges) {
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

        ///////////////////////////////////////////////////////////
        ListGraph<CityClass> testGraph = new ListGraph<>();
        DijkstraPathFinder<CityClass> lowestWeightFinder = new DijkstraPathFinder<>();

        // 1. Skapa städer
        CityClass A = new CityClass("A");
        CityClass B = new CityClass("B");
        CityClass C = new CityClass("C");
        CityClass D = new CityClass("D");
        CityClass E = new CityClass("E");
        CityClass F = new CityClass("F");
        CityClass G = new CityClass("G");

        testGraph.add(A);
        testGraph.add(B);
        testGraph.add(C);
        testGraph.add(D);
        testGraph.add(E);
        testGraph.add(F);
        testGraph.add(G);

        testGraph.connect(A, B, "", 2);
        testGraph.connect(A, D, "", 5);
        testGraph.connect(A, F, "", 3);
        testGraph.connect(B, C, "", 7);
        testGraph.connect(B, E, "", 1);
        testGraph.connect(B, F, "", 4);
        testGraph.connect(C, E, "", 3);
        testGraph.connect(C, G, "", 4);
        testGraph.connect(D, E, "", 1);
        testGraph.connect(D, G, "", 1);
        testGraph.connect(E, G, "", 3);

        // System.out.println("\nVÄGEN MED LÄGST VIKT: ");

        Path<CityClass> testResult = lowestWeightFinder.findPath(testGraph, A, C);
        System.out.println("Till C: " + testResult.getTotalWeight());
        testResult = lowestWeightFinder.findPath(testGraph, A, E);
        System.out.println("Till E: " + testResult.getTotalWeight());
        testResult = lowestWeightFinder.findPath(testGraph, A, G);
        System.out.println("Till G: " + testResult.getTotalWeight());
        testResult = lowestWeightFinder.findPath(testGraph, A, F);
        System.out.println("Till F: " + testResult.getTotalWeight());
        testResult = lowestWeightFinder.findPath(testGraph, A, D);
        System.out.println("Till D: " + testResult.getTotalWeight());
    }
}
