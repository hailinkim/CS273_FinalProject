package BellmanFord;

import java.util.*;

public class GraphGenerator {

    // Generate a random weighted directed graph in the int graph[][] format.
    public static int[][] generateGraph(int numVertices, int numEdges) {
        int[][] graph = new int[numEdges][3];
        Random random = new Random();

        // Generate random edges with random weights
        for (int i = 0; i < numEdges; i++) {
            int from = random.nextInt(numVertices);
            int to = random.nextInt(numVertices);
            int weight = random.nextInt(20) - 10; // Generate random weight between -10 and 10

            graph[i][0] = from;
            graph[i][1] = to;
            graph[i][2] = weight;
        }

        // Ensure no negative weight cycles
        int[] distances = new int[numVertices];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[0] = 0;
        for (int i = 0; i < numVertices - 1; i++) {
            for (int j = 0; j < numEdges; j++) {
                int from = graph[j][0];
                int to = graph[j][1];
                int weight = graph[j][2];
                if (distances[from] != Integer.MAX_VALUE && distances[to] > distances[from] + weight) {
                    distances[to] = distances[from] + weight;
                }
            }
        }
        for (int j = 0; j < numEdges; j++) {
            int from = graph[j][0];
            int to = graph[j][1];
            int weight = graph[j][2];
            if (distances[from] != Integer.MAX_VALUE && distances[to] > distances[from] + weight) {
                // Negative weight cycle detected, regenerate graph
                return generateGraph(numVertices, numEdges);
            }
        }

        return graph;
    }
}
