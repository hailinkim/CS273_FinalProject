package BellmanFord;

import java.util.*;

public class GraphGenerator {
    public static Random random = new Random();
    // Generate a random weighted directed graph.
    // Our graph is represented as an adjacency matrix, so that each cell in the matrix represents an edge between two vertices.
    // If there is an edge between vertex i and vertex j, then matrix[i][j] = non-zero weight.
    //Note that this method assumes that the graph is undirected, thus generating a symmetric adjacency matrix.
    public static int[][] generateGraph(int numVertices, int numEdges) {
        int[][] matrix = new int[numVertices][numVertices];

        Random random = new Random();
        int count = 0;
        int weight;

        while (count < numEdges) {
            int i = random.nextInt(numVertices);
            int j = random.nextInt(numVertices);

            if (i == j || matrix[i][j] != 0) {
                continue;
            }
            do {
                weight = random.nextInt(21) - 10; //generate a weight between -10 and 10
            } while (weight == 0);  //make sure we have non-zero weights

            matrix[i][j] = weight;
            matrix[j][i] = weight;
            count++;
        }

        // Detect and eliminate negative weight cycles
        eliminateNegativeWeightCycles(matrix);

        return matrix;
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