package BellmanFord;

import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;

public class GraphGenerator {
   public static Random random = new Random();

    // Generate a random weighted directed graph.
    // Our graph is represented as a adjacency matrix, so that each cell in the matrix represents an edge between two vertices.
    //If there is an edge between vertex i and vertex j, then matrix[i][j]= weight.
    public static  int[][] generateGraph(int numVertices, int numEdges) {
        int[][] graph = new int[numVertices][numVertices];
        // Map<Integer,Integer> edge = new ConcurrentHashMap<>();
        // Map<Integer, Map<Integer,Integer>> graphMap = new ConcurrentHashMap<>();
        // graph = < source vertex : <Destination : Weight>>

        // Generate numEdges number of random edges, that start from a random 'source' vertex,
        // end at a random outgoing neighbor, 'destination' vertex, with a random weight 'weight.'
        for (int i = 0; i < numEdges; i++) {
            int source = random.nextInt(numVertices);
            int destination = random.nextInt(numVertices);
            int weight = random.nextInt(20) - 10; // Generate random weight between -10 and 10

            // check that source and destination are different numbers, since we don't want an edge that goes FROM a vertex TO the same vertex.
            // also check that the destination isn't already mapped to the source via another edge.
            while (destination==source || graph[source][destination] !=0){
                destination = random.nextInt(numVertices); // regenerate 'destination'
            }
            graph[source][destination] = weight;

            //edge.put(destination,weight); // there can only be 1 weight mapped to each destination.
            // Add inner maps to the outer map
            //graphMap.put(source, edge);
        }

        return graph;
    }
}
