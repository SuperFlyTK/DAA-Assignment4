package graph.dagsp;

import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathTest {

    @Test
    void testShortestPathInDAG() {
        Graph graph = new Graph(6, true, "edge");
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 6);
        graph.addEdge(2, 4, 4);
        graph.addEdge(2, 5, 2);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 3);

        DAGShortestPath dagSP = new DAGShortestPath();
        PathResult result = dagSP.findShortestPath(graph, 0);

        int[] distances = result.getDistances();
        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(11, distances[3]); // 0->1->3 = 5+6=11
        assertEquals(7, distances[4]);  // 0->2->4 = 3+4=7
        assertEquals(5, distances[5]);  // 0->2->5 = 3+2=5
    }

    @Test
    void testCriticalPath() {
        // Simple linear graph
        Graph graph = new Graph(3, true, "edge");
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);

        DAGShortestPath dagSP = new DAGShortestPath();
        CriticalPathResult result = dagSP.findCriticalPath(graph);

        assertEquals(8, result.getLength()); // 0->1->2 = 5+3=8
        List<Integer> criticalPath = result.getCriticalPath();
        assertEquals(3, criticalPath.size());
        assertEquals(0, (int) criticalPath.get(0));
        assertEquals(1, (int) criticalPath.get(1));
        assertEquals(2, (int) criticalPath.get(2));
    }

    @Test
    void testPathReconstruction() {
        Graph graph = new Graph(3, true, "edge");
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);

        DAGShortestPath dagSP = new DAGShortestPath();
        PathResult result = dagSP.findShortestPath(graph, 0);

        List<Integer> path = result.reconstructPath(2);
        assertEquals(3, path.size());
        assertEquals(0, (int) path.get(0));
        assertEquals(1, (int) path.get(1));
        assertEquals(2, (int) path.get(2));
    }

    @Test
    void testLongestPath() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 2);

        DAGShortestPath dagSP = new DAGShortestPath();
        PathResult result = dagSP.findLongestPath(graph, 0);

        int[] distances = result.getDistances();
        assertEquals(0, distances[0]);
        assertEquals(2, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(6, distances[3]); // 0->1->3 = 2+4=6
    }

    @Test
    void testCriticalPathWithMultiplePaths() {
        Graph graph = new Graph(5, true, "edge");
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 5);  // This path is longer: 1+5=6 vs 2+4=6
        graph.addEdge(3, 4, 3);

        DAGShortestPath dagSP = new DAGShortestPath();
        CriticalPathResult result = dagSP.findCriticalPath(graph);

        // Critical path: 0->2->3->4 = 1+5+3=9
        assertEquals(9, result.getLength());
        List<Integer> criticalPath = result.getCriticalPath();

        // Check start and end nodes
        assertEquals(0, (int) criticalPath.get(0));
        assertEquals(4, (int) criticalPath.get(criticalPath.size() - 1));

        // Check that path contains node 3 (should be in the path)
        assertTrue(criticalPath.contains(3));

        // Remove the problematic assertion that checks for node 2
        // The algorithm might choose a different valid path with same length
    }

    @Test
    void testCriticalPathSingleNode() {
        Graph graph = new Graph(1, true, "edge");

        DAGShortestPath dagSP = new DAGShortestPath();
        CriticalPathResult result = dagSP.findCriticalPath(graph);

        assertEquals(0, result.getLength());
        List<Integer> criticalPath = result.getCriticalPath();
        assertEquals(1, criticalPath.size());
        assertEquals(0, (int) criticalPath.get(0));
    }

    @Test
    void testCriticalPathClearWinner() {
        // Make one path clearly longer than others
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 1);   // Short path
        graph.addEdge(0, 2, 10);  // Long path - clear winner
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        DAGShortestPath dagSP = new DAGShortestPath();
        CriticalPathResult result = dagSP.findCriticalPath(graph);

        assertEquals(11, result.getLength()); // 0->2->3 = 10+1=11
        List<Integer> criticalPath = result.getCriticalPath();

        // Should definitely go through node 2
        assertTrue(criticalPath.contains(2));
        assertEquals(0, (int) criticalPath.get(0));
        assertEquals(3, (int) criticalPath.get(criticalPath.size() - 1));
    }
}