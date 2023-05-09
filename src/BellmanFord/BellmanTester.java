package BellmanFord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BellmanTester {
    public static final int WARMUP_ITERATIONS = 5;
    public static final int TEST_ITERATIONS = 1;

    static int MAX_NUM_VERTICES = 100; // Number of vertices in graph
    static int MAX_NUM_EDGES = 2000; // Number of edges in graph, must be less than NUM_VERTICES ^ 2.
    static int[][] testgraph = new int[MAX_NUM_VERTICES][MAX_NUM_EDGES];

    public static void main(String[] args) {
        System.out.println("Generating new random weighted directed graph with no negative weight cycles....");
        testgraph = GraphGenerator.generateGraph(MAX_NUM_VERTICES, MAX_NUM_EDGES);
        System.out.println("finished generating graph.");
        // System.out.println("the generated graph is: \n");
//         printGraph(testgraph);

        List<Integer> sourceVertices = getNonZeroIndices(testgraph);
//        System.out.println("here is the list of vertices in our graph with an outgoing edge: " );
//        sourceVertices.forEach(element -> System.out.print(element + "  "));
        int u = getRandomElement(sourceVertices);
        System.out.printf("\nOur randomly selected starting vertex (u) is vertex %d.\n\n",  u);

        int[] knownShortestDistance = new int[testgraph.length];
        int[] testShortestDistance = new int[testgraph.length];

        long start = System.nanoTime();
        BellmanFord.bellmanFordBaseline(knownShortestDistance, testgraph, u);
        long elapsedMS = (System.nanoTime() - start) / 1_000_000;
        System.out.println("elapsed time for baseline:  " + elapsedMS + " ms");

        // run warmup before timing.
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            ParallelBellmanFord.bellmanFordOptimized(testShortestDistance, testgraph, u);
        }
        System.out.println("Warmup finished.");

        // run main iterations to test BellmanFordBaseline performance.
        System.out.println("testing BellmanFordBaseline (test iteration = " + TEST_ITERATIONS + " times).\n");

                // run main iterations to test BellmanFordBaseline performance.
        long start2 = System.nanoTime();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            ParallelBellmanFord.bellmanFordOptimized(testShortestDistance, testgraph, u);
        }
        long elapsedMS2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("elapsed time for optimized:  " + elapsedMS2 + " ms");


        for (int i = 0; i < knownShortestDistance.length; i++) {
            if (knownShortestDistance[i] != testShortestDistance[i]) {
                System.out.println("correctness test failed\n" +
                        "i = " + i + "\n" +
                        "knownShortestDistance[i] = " + knownShortestDistance[i] + "\n" +
                        "testShortestDistance[i] = " + testShortestDistance[i]);
                return;
            }
        }
        System.out.println("correctness test succeeded.");
    }
    public static Integer getRandomElement(List<Integer> list) {
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
    public static List<Integer> getNonZeroIndices(int[][] matrix) {
       List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if(matrix[i][j]!=0) {
                    if(!indices.contains(i))
                        indices.add(i);
                    if(!indices.contains(j))
                        indices.add(j);
                }
            }
        }
//        System.out.println("Indices of nonzero entries: " + indices);
        return indices;
    }

    // Print the adjacency matrix
    public static void printGraph(int[][] graph) {
        int numVertices = graph.length;

        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                System.out.print(graph[i][j] + "  ");
            }
            System.out.println();
        }
    }
}

// below is an example edge list graph with no negative weight cycles.
//    static final int[][] testgraph = {
//            { 0, 1, 4 },
//            { 0, 3, 3 },
//            { 1, 2, -1 },
//            { 1, 4, 2 },
//            { 2, 5, 1 },
//            { 2, 8, -2 },
//            { 3, 6, 3 },
//            { 3, 9, -4 },
//            { 4, 7, -5 },
//            { 4, 10, 6 },
//            { 5, 11, 2 },
//            { 5, 14, 5 },
//            { 6, 12, -3 },
//            { 6, 15, 4 },
//            { 7, 13, 1 },
//            { 7, 16, 2 },
//            { 8, 17, 3 },
//            { 8, 21, -1 },
//            { 9, 18, 2 },
//            { 9, 22, 1 },
//            { 10, 19, 2 },
//            { 10, 23, -3 },
//            { 11, 20, -5 },
//            { 11, 24, 4 },
//            { 12, 25, 5 },
//            { 12, 28, -2 },
//            { 13, 26, 3 },
//            { 13, 29, -4 },
//            { 14, 27, 2 },
//            { 14, 30, 6 },
//            { 15, 31, 1 },
//            { 15, 34, -5 },
//            { 16, 32, 2 },
//            { 16, 35, 3 },
//            { 17, 18, -4 },
//            { 18, 19, -3 },
//            { 19, 20, -2 },
//            { 20, 21, -1 },
//            { 21, 22, 2 },
//            { 22, 23, 3 },
//            { 23, 24, 4 },
//            { 24, 25, -5 },
//            { 25, 26, -4 },
//            { 26, 27, -3 },
//            { 27, 28, -2 },
//            { 28, 29, -1 },
//            { 29, 30, 2 },
//            { 30, 31, 3 },
//            { 31, 32, 4 },
//            { 32, 33, -5 },
//            { 33, 34, -4 },
//            { 34, 35, -3 }
//    };
//    static final int NUM_VERTICES = 36; // Number of vertices in graph
//    static final int NUM_EDGES =50; // Number of edges in graph