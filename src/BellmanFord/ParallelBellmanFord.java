package BellmanFord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import jdk.incubator.vector.*;
public class ParallelBellmanFord {
    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    static class BellmanFordTask implements Runnable {
        private int[][] graph;
        private int start;
        private int end;
        private int[] currentShortestDistance;

        public BellmanFordTask(int[][] graph, int start, int end, int[] currentShortestDistance) {
            this.graph = graph;
            this.start = start;
            this.end = end;
            this.currentShortestDistance = currentShortestDistance;
        }
        public void run() {
            for (int edge = start; edge < end; edge++) {
                if (currentShortestDistance[graph[edge][0]] != Integer.MAX_VALUE &&
                        currentShortestDistance[graph[edge][0]] + graph[edge][2] < currentShortestDistance[graph[edge][1]]){
                    currentShortestDistance[graph[edge][1]] = currentShortestDistance[graph[edge][0]] + graph[edge][2];
                }
            }
        }
    }

    public static void bellmanFordOptimized(int[] currentShortestDistance, int[][] graph, int numVertices, int numEdges, int u) {
        Arrays.fill(currentShortestDistance, 0, currentShortestDistance.length, Integer.MAX_VALUE);
        currentShortestDistance[u] = 0;
        System.out.println(Arrays.toString(currentShortestDistance));

        ExecutorService executorService = Executors.newFixedThreadPool(2); //Runtime.getRuntime().availableProcessors()
        int step = numEdges / 2;
                //Runtime.getRuntime().availableProcessors();

        for (int i = 1; i <= numVertices-1; i++) {
            List<Future<?>> futures = new ArrayList<>();
            //parallelize the inner loop
            for(int j = 0; i < numEdges; j+=step){
                int start = j;
                int end = Math.min(j + step, numEdges-1);
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
            System.out.println("futures: " + futures);
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
