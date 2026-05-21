package se.su.inlupp;
import java.util.Collection;
import java.util.Set;

public class TravelPlannerModel {
    private Graph<City> graph;
    private PathFinder<City> pathFinder;

    public TravelPlannerModel(){
        graph = new ListGraph<>();
    }

    public void addCity(City city){
        graph.add(city);
    }

    public void removeCity(City city){
        graph.remove(city);
    }

    public boolean hasCity(City city){
       return graph.hasNode(city);
    }

    public Set<City> getCities(){
        return graph.getNodes();
    }

     public void addRoute(City from, City to, String name, int weight) {
        graph.connect(from, to, name, weight);
    }

    public void removeRoute(City from, City to) {
        graph.disconnect(from, to);
    }

    public Edge<City> getRouteBetween(City from, City to) {
        return graph.getEdgeBetween(from, to);
    }

    public Collection<Edge<City>> getRoutesFrom(City city) {
        return graph.getEdgesFrom(city);
    }

     public Graph<City> getGraph() {
        return graph;
    }

    public void setGraph(Graph<City> graph) {
        this.graph = graph;
    }
    
}
