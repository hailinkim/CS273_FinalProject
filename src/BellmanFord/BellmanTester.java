package BellmanFord;


import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class BellmanTester {
    public static final int WARMUP_ITERATIONS = 5;
    public static final int TEST_ITERATIONS = 30;

    static final int NUM_VERTICES = 10; // Number of vertices in graph
    static final int NUM_EDGES = 30; // Number of edges in graph
    static int [][] testgraph = new int[NUM_VERTICES][NUM_EDGES];

    public static void main(String[] args) {
        System.out.println("generating new random weighted directed graph with no negative weight cyles.");
        System.out.println("Number of vertices: " + NUM_VERTICES + ", Number of edges: " + NUM_EDGES);
        testgraph = new int[][]{{8, 2, -3},
                {5, 8, -9},
                {8, 4, 5},
                {4, 2, 1},
                {4, 3, -3},
                {7, 2, -9},
                {2, 2, 2},
                {1, 9, -7},
                {3, 6, 0},
                {1, 5, -9},
                {2, 8, 7},
                {3, 4, -5},
                {7, 9, -4},
                {1, 5, -3},
                {2, 8, -2},
                {2, 3, 4},
                {8, 1, -9},
                {1, 1, -6},
                {6, 5, -10},
                {3, 2, -1},
                {2, 8, 5},
                {3, 1, -4},
                {8, 7, 4},
                {9, 4, 0},
                {2, 8, -4},
                {2, 8, 6},
                {1, 8, 0},
                {8, 4, 9},
                {5, 4, 9},
                {8, 9, 2}};


//        testgraph = GraphGenerator.generateGraph(NUM_VERTICES,NUM_EDGES);
        System.out.println("finished generating graph.");
//        System.out.println("the generated graph is: \n");
        printgraph(testgraph);

        int[] source = Arrays.stream(testgraph).mapToInt(ints -> ints[0]).toArray();
        int u = 4;
                //source[new Random().nextInt(source.length)];
        System.out.println("start: "+ u);
        int[] knownShortestDistance = new int[NUM_VERTICES];
        int[] testShortestDistance = new int[NUM_VERTICES];

        BellmanFord.bellmanFordBaseline(knownShortestDistance, testgraph, NUM_VERTICES, NUM_EDGES, u);
        System.out.println("baseline done\n");


        System.out.println("starting warmup.\n");
        // run warmup before timing
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            ParallelBellmanFord.bellmanFordOptimized(testShortestDistance, testgraph, NUM_VERTICES, NUM_EDGES, u);
        }
        System.out.println("warmup finished.\n");

//        System.out.println("testing BellmanFordBaseline (test iteration = " + TEST_ITERATIONS + " times).\n");
//        // run main iterations to test BellmanFordBaseline performance.
//        long start = System.nanoTime();
//        for (int i = 0; i < TEST_ITERATIONS; i++) {
//            ParallelBellmanFord.bellmanFordOptimized(testShortestDistance, testgraph, NUM_VERTICES, NUM_EDGES, u);
//        }
//        long elapsedMS = (System.nanoTime() - start) / 1_000_000;
//
//        for (int i = 0; i < knownShortestDistance.length; i++) {
//            if (knownShortestDistance[i] != testShortestDistance[i]) {
//                System.out.println("correctness test failed\n" +
//                        "i = " + i + "\n" +
//                        "knownPrimes[i] = " + knownShortestDistance[i] + "\n" +
//                        "testPrimes[i] = " + testShortestDistance[i]);
//                return;
//            }
//        }
//
//        System.out.println(Arrays.toString(knownShortestDistance));
//        System.out.println(Arrays.toString(testShortestDistance));
//        System.out.println("elapsed time: " + elapsedMS + " ms");
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