package graph.dagsp;

import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathTest {

    @Test
    void testShortestPathInDAG() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 6);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 4, 4);
        graph.addEdge(2, 5, 2);
        graph.addEdge(2, 3, 7);
        graph.addEdge(3, 4, -1);
        graph.addEdge(4, 5, -2);

        DAGShortestPath dagSP = new DAGShortestPath();
        PathResult result = dagSP.findShortestPath(graph, 0);

        int[] distances = result.getDistances();
        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(10, distances[3]); // 0->2->3 = 3+7=10
    }

    @Test
    void testCriticalPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 5);

        DAGShortestPath dagSP = new DAGShortestPath();
        CriticalPathResult result = dagSP.findCriticalPath(graph);

        assertEquals(7, result.getLength()); // 0->2->3 = 2+5=7
        List<Integer> criticalPath = result.getCriticalPath();
        assertTrue(criticalPath.contains(0) && criticalPath.contains(2) && criticalPath.contains(3));
    }
}