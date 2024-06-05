
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Queue;
        import java.util.HashMap;
        import java.util.ArrayDeque;
        import java.util.Collection;
        import java.util.Map;
        import javafx.util.Pair;

/**
 * Implementation of Edmonds-Karp algorithm to find augmentation paths and network flow.
 */

public class EdmondKarp {
    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges = new HashMap<>();

    public static void computeResidualGraph(Graph graph){
        // Get original edges from the graph
        Collection<Edge> forwardEdges = graph.getOriginalEdges();
        // Add forward Edges to the map
        int id = 0;
        for (Edge e : forwardEdges){
            // Set the flow to 0
            e.setFlow(0);
            // Put edge in edges map
            edges.put(id+"",e);
            // Add edge to edgeIds collections in both the from & to city
            e.fromCity().addEdgeId(id+"");
            e.toCity().addEdgeId(id+"");
            // Increment count for even numbers
            id ++;
            City fromCity = e.fromCity();
            City toCity = e.toCity();

            Edge reverseEdge = new Edge(toCity,fromCity,e.transpType(),0,0);
            edges.put(id+"", reverseEdge);

            e.fromCity().addEdgeId(id+"");
            e.toCity().addEdgeId(id+"");

            id++;
        }
        printResidualGraphData(graph);  //may help in debugging
    }

    // Method to print Residual Graph
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());
            // for each city display the out edges
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge)->
                System.out.println("["+eId+"] " +edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph.
    //=============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id){
        return edges.get(id);
    }

    /**
     * Udates the flow to reflect bottleneck value.
     */

    public static void updateFlow(ArrayList<String> path, int bottleneck) {
        for (String edgeId : path) {
            Edge e = edges.get(edgeId);
            e.setFlow(e.flow() + bottleneck);
        }
    }

    /**
     * Calculates the bottleneck of an augmentation path
     */
    public static int bottleneck(ArrayList<String> path) {
        int lowestCap = Integer.MAX_VALUE;
        for (String edgeId : path) {
            Edge e = edges.get(edgeId);
            // Calculate remaining capacity
            int remainingCap = e.capacity() - e.flow();
            if (remainingCap < lowestCap) {
                lowestCap = remainingCap;
            }
        }
        return lowestCap;
    }

    /**
     * Finds maximum flow through a graph from start city to sink city.
     */
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        ArrayList<Pair<ArrayList<String>, Integer>> augmentationPathsAll = new ArrayList<>();

        computeResidualGraph(graph);

        while (true) {

            Pair<ArrayList<String>, Integer> path = bfs(graph, from, to);

            if (path == null || path.getKey().isEmpty()) {
                break;
            }

            augmentationPathsAll.add(path);
            updateFlow(path.getKey(), path.getValue());
        }
        return augmentationPathsAll;
    }

    /**
     * Runs a BFS on the graph from start city (s) --> sink city (t).
     * Returns an augmentation path along with the bottleneck value for the path.
     */
    public static Pair<ArrayList<String>, Integer> bfs(Graph graph, City s, City t) {

        HashMap<String, String> backPointer = new HashMap<>();

        Queue<City> queue = new ArrayDeque<>();

        queue.offer(s);
        while (!queue.isEmpty()) {

            City current = queue.poll();

            for (String edgeId : current.getEdgeIds()) {

                Edge e = edges.get(edgeId);
                if (!(e.toCity().equals(s)) && backPointer.get(e.toCity().getId()) == null && e.capacity() - e.flow() > 0) {

                    backPointer.put(e.toCity().getId(), edgeId);

                    if (e.toCity().equals(t)) {
                        ArrayList<String> augmentationPath = new ArrayList<>();

                        String cityId = t.getId();
                        while (backPointer.containsKey(cityId)) {
                            String edge = backPointer.get(cityId);
                            augmentationPath.add(edge);
                            cityId = edges.get(edge).fromCity().getId();
                        }
                        Collections.reverse(augmentationPath);

                        int bottleneck = bottleneck(augmentationPath);

                        return new Pair<>(augmentationPath, bottleneck);
                    }

                    queue.offer(e.toCity());
                }
            }
        }
        // If no augmentation path is found, return null
        return null;
    }
}