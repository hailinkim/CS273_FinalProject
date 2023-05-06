package BellmanFord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

import jdk.incubator.vector.*;
//import java.util.concurrent.atomic.AtomicIntegerArray;
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
                    if(start == 15){
                        System.out.println("edge " + edge + ": " + currentShortestDistance[graph[edge][0]]);
                        System.out.println("src: " + currentShortestDistance[graph[edge][0]] + ",dest: " + currentShortestDistance[graph[edge][1]] +
                                ", weight: " + graph[edge][2]);
                        System.out.println("edge " + edge + ": " + currentShortestDistance[graph[edge][0]]);
                        System.out.println(graph[edge][2]);
                        System.out.println(currentShortestDistance[graph[edge][0]] +graph[edge][2]);
                    }
                    currentShortestDistance[graph[edge][1]] = currentShortestDistance[graph[edge][0]] + graph[edge][2];
                    if(start == 15){
                        System.out.println("new dest: " + currentShortestDistance[graph[edge][1]]);
                    }
                }
            }
        }
    }

    public static void bellmanFordOptimized(int[] currentShortestDistance, int[][] graph, int numVertices, int numEdges, int u) {
        Arrays.fill(currentShortestDistance, 0, currentShortestDistance.length, Integer.MAX_VALUE);
        currentShortestDistance[u] = 0;

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int step = numEdges / Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < numVertices-1; i++) {
            List<Future<?>> futures = new ArrayList<>();
            //parallelize the inner loop
            for(int j = 0; j < numEdges; j+=step){
                int start = j;
                int end = Math.min(j + step, numEdges);
                futures.add(executorService.submit(new BellmanFordTask(graph, start, end, currentShortestDistance)));
            }
            for(Future<?> future: futures){
                try {
                    future.get();
//                    for (int k = 0; k < numVertices; i++) {
//                        if (result[k] < currentShortestDistance[k]) {
//                            currentShortestDistance[k] = result[k];
//                        }
//                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            futures.clear();
//            System.out.println("futures: " + futures);
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
