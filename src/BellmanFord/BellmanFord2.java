package BellmanFord;

//  Baseline Implementation of Bellman-Ford's single source shortest path (SSSP) algorithm.
//  UPDATE to the baseline implementation: uses Adjacency Matrix representation of a graph, instead of an edge list representation.
class BellmanFord2
{
    // A baseline function that finds shortest distances from u
    // to all other vertices
    // using the Bellman-Ford algorithm.
    static void bellmanFordBaseline2(int[] currentShortestDistance, int graph[][],  int startingVertexU)
    {
        int numVertices = graph.length;
        for (int i = 0; i < numVertices; i++){
            if(i!=startingVertexU)
                currentShortestDistance[i] = Integer.MAX_VALUE; // initialize to max int for now.
        }

        // Relax edges repeatedly
        for (int j_hop = 0; j_hop < numVertices - 1; j_hop++) {
            for (int u = 0; u < numVertices; u++) {
                //graph[source] --> extract out the index of nonzero elements (aka neighbors)
                for (int v = 0; v < numVertices; v++) {
                    // if there's an edge between the source and destination, and if the current dist[source] isn't inifinity,
                    // AND if the currentShortestDistance[u] + graph[u][v] is LESS THAN currentShortestDistance[v],
                    if (graph[u][v] != 0 && currentShortestDistance[u] != Integer.MAX_VALUE &&
                            currentShortestDistance[u] + graph[u][v] < currentShortestDistance[v]) {
                                    currentShortestDistance[v] = currentShortestDistance[u] + graph[u][v]; // then update the currentShortestDistance[v].
                    }
                }
            }
        }
    }
}
