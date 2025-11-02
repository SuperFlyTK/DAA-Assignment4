package graph.topo;

import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalSortTest {

    @Test
    void testTopologicalSortDAG() {
        Graph graph = new Graph(6, true, "edge");
        graph.addEdge(5, 2, 1);
        graph.addEdge(5, 0, 1);
        graph.addEdge(4, 0, 1);
        graph.addEdge(4, 1, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1);

        TopologicalSort topo = new TopologicalSort();
        TopoResult result = topo.kahnAlgorithm(graph);

        assertFalse(result.hasCycle());
        List<Integer> order = result.getTopologicalOrder();
        assertEquals(6, order.size());

        // Verify topological order constraints
        assertTrue(order.indexOf(5) < order.indexOf(2));
        assertTrue(order.indexOf(2) < order.indexOf(3));
        assertTrue(order.indexOf(3) < order.indexOf(1));
        assertTrue(order.indexOf(4) < order.indexOf(0));
        assertTrue(order.indexOf(4) < order.indexOf(1));
    }

    @Test
    void testTopologicalSortWithCycle() {
        Graph graph = new Graph(3, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1); // Cycle

        TopologicalSort topo = new TopologicalSort();
        TopoResult result = topo.kahnAlgorithm(graph);

        assertTrue(result.hasCycle());
    }
}