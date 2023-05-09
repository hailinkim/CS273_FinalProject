package BellmanFord;

import java.util.*;

public class AdjacencyMatrixGraphGenerator {
    public static Random random = new Random();
    // Generate a random weighted directed graph.
    // Our graph is represented as an adjacency matrix, so that each cell in the matrix represents an edge between two vertices.
    // If there is an edge between vertex i and vertex j, then matrix[i][j] = weight.
    public static int[][] generateGraph(int numVertices, int numEdges) {
        int[][] graph = new int[numVertices][numVertices];
        // Generate numEdges number of random edges, that start from a random 'source' vertex,
        // end at a random 'destination' vertex, with a random weight 'weight.'
        for (int i = 0; i < numEdges; i++) {
            int source = random.nextInt(numVertices);
            int destination = random.nextInt(numVertices);
            int weight = random.nextInt(20) - 10; // Generate random weight between -10 and 10

            // Check that source and destination are different numbers, since we don't want an edge that goes FROM a vertex TO the same vertex.
            // Also, check that the destination isn't already mapped to the source via another edge.
            while (destination == source || graph[source][destination] != 0) {
                destination = random.nextInt(numVertices); // Regenerate 'destination'
            }
            graph[source][destination] = weight;
        }

        // Detect and eliminate negative weight cycles
        eliminateNegativeWeightCycles(graph);

        return graph;
    }

    // Detect and eliminate negative weight cycles using the Bellman-Ford algorithm
    private static void eliminateNegativeWeightCycles(int[][] graph) {
        int numVertices = graph.length;
        int[] distance = new int[numVertices];

        // Initialize distances to infinity
        Arrays.fill(distance, Integer.MAX_VALUE);

        // Set distance to the source vertex as 0
        distance[0] = 0;

        // Relax edges repeatedly
        for (int i = 0; i < numVertices - 1; i++) {
            for (int u = 0; u < numVertices; u++) {
                for (int v = 0; v < numVertices; v++) {
                    if (graph[u][v] != 0 && distance[u] != Integer.MAX_VALUE && distance[u] + graph[u][v] < distance[v]) {
                        distance[v] = distance[u] + graph[u][v];
                    }
                }
            }
        }

        // Check for negative weight cycles and set their weights to 0
        for (int u = 0; u < numVertices; u++) {
            for (int v = 0; v < numVertices; v++) {
                if (graph[u][v] != 0 && distance[u] != Integer.MAX_VALUE && distance[u] + graph[u][v] < distance[v]) {
                    graph[u][v] = 0;
                }
            }
        }
    }
}