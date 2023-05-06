package BellmanFord;//  Baseline Implementation of Bellman-Ford's single source shortest path (SSSP) algorithm.
class BellmanFord
{

    // A baseline function that finds shortest distances from u
    // to all other vertices
    // using the Bellman-Ford algorithm.
    // Also decects if there are any negative weight cycles.
    static void bellmanFordBaseline(int[] currrentShortestDistance, int graph[][], int numVertices, int numEdges,  int u)
    {
        for (int i = 0; i < numVertices; i++){
            if(i!=u)
                currrentShortestDistance[i] = Integer.MAX_VALUE; // initialize to max int for now.
        }

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
                if (currrentShortestDistance[graph[edge][0]] != Integer.MAX_VALUE &&
                        currrentShortestDistance[graph[edge][0]] + graph[edge][2] < currrentShortestDistance[graph[edge][1]]){
                    currrentShortestDistance[graph[edge][1]] = currrentShortestDistance[graph[edge][0]] + graph[edge][2];
                }
            }
        }

        // check for negative-weight cycles.
        // The above step guarantees shortest
        // distances if graph doesn't contain
        // negative weight cycle. If we get a
        // shorter path, then there is a cycle.
//        for (int i = 0; i < numEdges; i++)
//        {
//            int x = graph[i][0];
//            int y = graph[i][1];
//            int weight = graph[i][2];
//            if (currrentShortestDistance[x] != Integer.MAX_VALUE &&
//                    currrentShortestDistance[x] + weight < currrentShortestDistance[y])
//                System.out.println("Graph contains negative weight cycle!!!!");
//        }

//        System.out.println("Vertex\t\tDistance from Source");
//        for (int i = 0; i < numVertices; i++)
//            System.out.println(i + "\t\t\t\t" + currrentShortestDistance[i]);
    }
}