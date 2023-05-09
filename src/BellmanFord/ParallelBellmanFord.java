package BellmanFord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
public class ParallelBellmanFord {

    static class BellmanFordTask implements Runnable {
        private int[][] graph;
        private int start;
        private int end;
        private final int[] currentShortestDistance;

        public BellmanFordTask(int[][] graph, int start, int end, int[] currentShortestDistance) {
            this.graph = graph;
            this.start = start;
            this.end = end;
            this.currentShortestDistance = currentShortestDistance;
        }

        public void run() {
            for (int edge = start; edge < end; edge++) {
                // for every vertex edge[0] in this chunk of vertices from edge start to edge end,
                // 
                if (currentShortestDistance[graph[edge][0]] != Integer.MAX_VALUE &&
                        currentShortestDistance[graph[edge][0]] + graph[edge][2] < currentShortestDistance[graph[edge][1]]){
                    currentShortestDistance[graph[edge][1]] = currentShortestDistance[graph[edge][0]] + graph[edge][2];
                }
            }
        }
    }

    public static void bellmanFordOptimized(int[] currentShortestDistance, int[][] graph, int numVertices, int numEdges, int u) {
        //  the currentShortestDistance array will hold the shortest (least weighty) distance we've found from the source vertex u to all other vertices v.
        // first, initialize currentShortestDistance array for all vertices to infinity (integer max)
        Arrays.fill(currentShortestDistance, 0, currentShortestDistance.length, Integer.MAX_VALUE);
        // set currentShortestDistance array of u to 0, since the distance from u to u itself is 0.
        currentShortestDistance[u] = 0;

        ExecutorService executorService = Executors.newSingleThreadExecutor(); // will change to multiple threads soon
        int step = numEdges / Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < numVertices-1; i++) {
            List<Future<?>> futures = new ArrayList<>();
            /*
            * TO-DO:
            * inner loop should traverse the vertices, and parallelize the inner loop
            * each task updates the distance for a subset of vertices
            * each task should traverse all the neighbors of a given vertex
            * maybe we need a method that gives us the neighbors of a vertex in the graph?
            * */
            for(int j = 0; j < numEdges; j+=step){
                int start = j;
                int end = Math.min(j + step, numEdges);
                futures.add(executorService.submit(new BellmanFordTask(graph, start, end, currentShortestDistance)));
            }
            for(Future<?> future: futures){
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            futures.clear();
        }
        executorService.shutdown();


        // check for negative-weight cycles.
//        for (int i = 0; i < numEdges; i++) {
//            int x = graph[i][0];
//            int y = graph[i][1];
//            int weight = graph[i][2];
//            if (currentShortestDistance[x] != Integer.MAX_VALUE &&
//                    currentShortestDistance[x] + weight < currentShortestDistance[y]) {
//                System.out.println("Graph contains negative weight cycle!!!!");
//            }
//        }
    }
}


//                    System.arraycopy(result, 0, currentShortestDistance, 0, result.length);
//TO-DO: SIMD with masking
//                    for (int k = 0; k < numVertices; i++) {
//                        if (result[k] < currentShortestDistance[k]) {
//                            currentShortestDistance[k] = result[k];
//                        }
//                    }
