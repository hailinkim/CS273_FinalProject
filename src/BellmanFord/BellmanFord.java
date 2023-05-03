package BellmanFord;//  Baseline Implementation of Bellman-Ford's single source shortest path (SSSP) algorithm.

class BellmanFord
{
    // Driver code for simple testing
    public static void main(String[] args)
    {
        int n = 5; // Number of vertices in graph
        int m = 8; // Number of edges in graph

        // the graph is basically a list of i edges, where
        // edge is a list three values (to , from, weight),
        // (edge from vertex 'from' to vertx 'to', with weight 'weight').
        int graph[][] = {
                { 0, 1, -1 }, // edge from vertex 0 to 1, with weight -1.
                { 0, 2, 4 },  // edge from vertex 0 to 2, with weight 4.
                { 1, 2, 3 },  // edge from vertex 1 to 2, with weight 3.
                { 1, 3, 2 }, // edge from vertex 1 to 3, with weight 2.
                { 1, 4, 2 }, // edge from vertex 1 to 4, with weight 2.
                { 3, 2, 5 }, // edge from vertex 3 to 2, with weight 5.
                { 3, 1, 1 },  // edge from vertex 3 to 1, with weight 1.
                { 4, 3, -3 }  // edge from vertex 4 to 3, with weight -3.
        };

        BellmanFordBaseline(graph, n, m, 3);
    }

    // A baseline function that finds shortest distances from u
    // to all other vertices
    // using the Bellman-Ford algorithm.
    // Also decects if there are any negative weight cycles.
    static void BellmanFordBaseline(int graph[][], int numVertices, int numEdges,  int u)
    {
        // Initialize the array to hold the current shortest distances from u to all other vertices from vertex 0 to numVertices - 1.
        int [] currrentShortestDistance = new int[numVertices];
        for (int i = 0; i < numVertices; i++){
            currrentShortestDistance[i] = Integer.MAX_VALUE; // initialize to max int for now.
        }

        // initialize distance of u to u as 0
        currrentShortestDistance[u] = 0;

        // Relax all edges |numVerticies| - 1 times, since
        // simple path = path without any repeated edges --> can have at most |numVertices| - 1 edges.
        for (int j_hop = 0; j_hop < numVertices - 1; j_hop++) // for all vertices 0 to n-1,
        {
            for (int edge = 0; edge < numEdges; edge++) // examine all edges in the graph.
            {
                // edge[0] is the current vertex, out from which the edge is coming.
                // edge[1] is the next vertx, to which the edge is going.
                // compare current shortest distance to edge[1], with the distance from edge[0] + weight of edge from 0 to 1.
                // if the (distance from edge[0] + weight of edge from 0 to 1) is shorter than the current shortest distance to edge 1,
                // update current shortest distance.
                if (currrentShortestDistance[graph[edge][0]] != Integer.MAX_VALUE && currrentShortestDistance[graph[edge][0]] + graph[edge][2] < currrentShortestDistance[graph[edge][1]]){
                    currrentShortestDistance[graph[edge][1]] = currrentShortestDistance[graph[edge][0]] + graph[edge][2];
                }
            }
        }

        // check for negative-weight cycles.
        // The above step guarantees shortest
        // distances if graph doesn't contain
        // negative weight cycle. If we get a
        // shorter path, then there is a cycle.
        for (int i = 0; i < numEdges; i++)
        {
            int x = graph[i][0];
            int y = graph[i][1];
            int weight = graph[i][2];
            if (currrentShortestDistance[x] != Integer.MAX_VALUE &&
                    currrentShortestDistance[x] + weight < currrentShortestDistance[y])
                System.out.println("Graph contains negative weight cycle!!!!");
        }

//        System.out.println("Vertex\t\tDistance from Source");
//        for (int i = 0; i < numVertices; i++)
//            System.out.println(i + "\t\t\t\t" + currrentShortestDistance[i]);
    }
}