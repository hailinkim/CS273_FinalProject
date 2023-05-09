package BellmanFord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class ParallelBellmanFord2 {
    static class BellmanFordTask implements Runnable {
        private int[][] graph;
        private int start;
        private int end;
        private final int[] neighbors;
        private final int[] dist;

        public BellmanFordTask(int[][] graph, int start, int end, int[] neighbors, int[] dist) {
            this.graph = graph;
            this.start = start;
            this.end = end;
            this.neighbors = neighbors;
            this.dist = dist;
        }

        public void run() {
            for (int u = start;  u < end; u++){
                for (int i = 0; i < neighbors.length; i++) {
                    int v= neighbors[i];
                    // if there's an edge between the source and destination, and if the current dist[source] isn't inifinity,
                    // AND if the currentShortestDistance[u] + graph[u][v] is LESS THAN currentShortestDistance[v],
                    if (graph[u][v] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][v] < dist[i]) {
                        dist[v] = dist[u] + graph[u][v]; // then update the currentShortestDistance[v].
                    }
                }
            }
        }
    } // End of BellmanFord Task

    // A parallelized function that finds the shortest distances from u to all other vertices
    // using the Bellman-Ford algorithm and multithreading.
    static void bellmanFordOptimized2(int[] dist, int graph[][],  int startingVertexU) {
        Arrays.fill(dist, 0, dist.length, Integer.MAX_VALUE);
        dist[startingVertexU] = 0;

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int numVertices = graph.length;
        int step = 2; //numVertices / Runtime.getRuntime().availableProcessors();

        // Relax edges repeatedly
        List<Future<?>> futures = new ArrayList<>();
        for (int j_hop = 0; j_hop < numVertices - 1; j_hop++) {
            for (int u = 0; u < numVertices; u+=step) {
                int[] adjacency = graph[u];
                int[] neighbors = IntStream.range(0,adjacency.length).filter(i -> adjacency[i] !=0).toArray();
                futures.add(executorService.submit(new BellmanFordTask(graph, u,  Math.min(u+ step, numVertices), neighbors, dist)));
            }
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            futures.clear();
        }
        executorService.shutdown();
    }
}
