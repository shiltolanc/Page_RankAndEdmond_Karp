import javafx.util.Pair;

import java.util.*;

/**
 * Write a description of class PageRank here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;
    /**
     * build the fromLinks and toLinks 
     */
    //TODO: Build the data structure to support Page rank. For each edge in the graph add the corresponding cities to the fromLinks and toLinks
    public static void computeLinks(Graph graph){
        // TODO
            for(Edge e: graph.getOriginalEdges()){
                e.fromCity().addToLinks(e.toCity());
                e.toCity().addFromLinks(e.fromCity());
            }
        printPageRankGraphData(graph);  ////may help in debugging
        // END TODO
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (City city : graph.getCities().values()){
            System.out.print("\nCity: "+city.toString());
            //for each city display the in edges 
            System.out.print("\nIn links to cities:");
            for(City c:city.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to cities:");
            //for each city display the out edges 
            for(City c: city.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;
        }    
        System.out.println("=================");
    }

    //TODO: Compute rank of all nodes in the network and display them at the console
    public static void computePageRank(Graph graph){
        // TODO
        int nNodes = graph.getCities().size();
        Map<City, Double> pageRank = new HashMap<>();

        graph.getCities().values().forEach(c -> pageRank.put(c, 1.0/nNodes));

        int count = 1;

        while (count <= iter){
            Map<City, Double> newRank = new HashMap<>();

            for(City node: graph.getCities().values()){
                double nRank = 0;
                for(City b: node.getFromLinks()){
                    double neighbourShare = pageRank.get(b) / b.getToLinks().size();
                    nRank += neighbourShare;
                }
                nRank = (1 - dampingFactor) / nNodes + dampingFactor * nRank;
                newRank.put(node, nRank);
            }
            pageRank.putAll(newRank);
            count++;
        }

        System.out.println("PageRank Values Iteration 10:\n");
        List<City> sortedKeys = new ArrayList<>(pageRank.keySet());
        java.util.Collections.sort(sortedKeys);
        for (City key : sortedKeys) {
            System.out.println(key + " = " + pageRank.get(key));
        }
        // END TODO
    }
}
