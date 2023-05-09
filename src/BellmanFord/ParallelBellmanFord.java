package BellmanFord;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ParallelBellmanFord {
    static class BellmanFordTask implements Runnable {
        private int[][] graph;
        //start ~ end: range of vertices handled by this task
        private int start;
        private int end;
        private final int[] dist;

        public BellmanFordTask(int[][] graph, int start, int end, int[] dist) {
            this.graph = graph;
            this.start = start;
            this.end = end;
            this.dist = dist;
        }

        public void run() {
            for (int u = start;  u < end; u++){
                int[] adjacency = graph[u];
                int[] neighbors = IntStream.range(0,adjacency.length).filter(i -> adjacency[i] !=0).toArray();
                //update the distance of each vertex by checking if the distance can be improved by going through one of its neighbors.
                for (int v : neighbors) {
                    if (dist[v] != Integer.MAX_VALUE) {
                        dist[u] = Math.min(dist[u], dist[v] + graph[u][v]);
                    }
                }
            }
        }
    }

    // A parallelized function that finds the shortest distances from a source vertex to every other vertices
    // using the Bellman-Ford algorithm and thread pools.
    static void bellmanFordOptimized(int[] dist, int graph[][], int startingVertexU) {
        Arrays.fill(dist, 0, dist.length, Integer.MAX_VALUE);
        dist[startingVertexU] = 0;

        int nThreads = Runtime.getRuntime().availableProcessors();
        int numVertices = graph.length;
        int nTasks = nThreads;
        int step = numVertices / nTasks; //# vertices to be handled by each thread -> #tasks = #threads


        //Approach 1: Futures
        // Relax edges repeatedly
//        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
//        List<Future<?>> futures = new ArrayList<>();
//        for (int j_hop = 0; j_hop < numVertices - 1; j_hop++) {
//            for (int u = 0; u < numVertices; u+= step) {
//                futures.add(executorService.submit(new BellmanFordTask(graph, u,  Math.min(u+ step, numVertices), dist)));
//            }
//            for (Future<?> future : futures) {
//                try {
//                    future.get();
//                } catch (InterruptedException | ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            futures.clear();
//        }
//        executorService.shutdown();
//        try {
//            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//        } catch (InterruptedException e) {
//            // blah
//        }


        //Approach 2: Thread pools
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        for (int j_hop = 0; j_hop < numVertices - 1; j_hop++) {
            for (int u = 0; u < numVertices; u+= step) {
                pool.execute(new BellmanFordTask(graph, u, Math.min(u+ step, numVertices), dist));
            }
        }
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // blah
        }


        //Approach 3: One Thread per Task
//        int nThreads2 = 200;
//        int step2 = numVertices/nThreads2;
//
//        Thread[] threads = new Thread[nThreads2];
//        for (int j_hop = 0; j_hop < numVertices - 1; j_hop++) {
//            int threadId = 0;
//            for (int u = 0; u < numVertices; u+= step2) {
//                threads[threadId++] = new Thread(new BellmanFordTask(graph, u, Math.min(u+ step, numVertices), dist));
//            }
//            for (Thread t : threads) {
//                t.start();
//            }
//
//            for (Thread t : threads) {
//                try {
//                    t.join();
//                } catch (InterruptedException e) {
//                    // don't care
//                }
//            }
//        }

    }
}
