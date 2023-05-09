package BellmanFord;
import java.util.Arrays;
import java.util.Random;

public class OldBellmanTester {
    public static final int WARMUP_ITERATIONS = 5;
    public static final int TEST_ITERATIONS = 1;

    static final int NUM_VERTICES = 1000; // Number of vertices in graph
    static final int NUM_EDGES = 3500; // Number of edges in graph
    static int [][] testgraph = new int[NUM_VERTICES][NUM_EDGES];

    public static void main(String[] args) {
        System.out.println("Generating new random weighted directed graph with no negative weight cyles....");
        testgraph = OldGraphGenerator.generateGraph(NUM_VERTICES,NUM_EDGES);
        System.out.println("finished generating graph.\n");
        System.out.println("Number of vertices: " + NUM_VERTICES + ", Number of edges: " + NUM_EDGES);

//        System.out.println("the generated graph is: \n");
//        printgraph(testgraph);

        int[] source = Arrays.stream(testgraph).mapToInt(ints -> ints[0]).toArray();
        int u = source[new Random().nextInt(source.length)];
        System.out.printf("our randomly selected starting vertex u is vertex %d.\n",  u);
        int[] knownShortestDistance = new int[NUM_VERTICES];
        int[] testShortestDistance = new int[NUM_VERTICES];

        // run warmup before timing.
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            OldParallelBellmanFord.bellmanFordOptimized(testShortestDistance, testgraph, NUM_VERTICES, NUM_EDGES, u);
        }
        System.out.println("Warmup finished.");

        // run main iterations to test BellmanFordBaseline performance.
        System.out.println("testing BellmanFordBaseline (test iteration = " + TEST_ITERATIONS + " times).\n");
        long start = System.nanoTime();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            OldBellmanFord.bellmanFordBaseline(knownShortestDistance, testgraph, NUM_VERTICES, NUM_EDGES, u);
        }
        long elapsedMS = (System.nanoTime() - start) / 1_000_000;
        System.out.println("elapsed time for baseline:  " + elapsedMS + " ms");

        // run main iterations to test BellmanFordBaseline performance.
        long start2 = System.nanoTime();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            OldParallelBellmanFord.bellmanFordOptimized(testShortestDistance, testgraph, NUM_VERTICES, NUM_EDGES, u);
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

    public static void printgraph(int[][] graph) {
        int i = 0;
        for (; i < graph.length; i++) {
            System.out.printf("{%d, %d, %d}\n", graph[i][0], graph[i][1], graph[i][2]);
        }
    }
}

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