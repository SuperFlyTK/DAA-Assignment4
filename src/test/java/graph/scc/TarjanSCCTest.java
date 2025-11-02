package graph.scc;

import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TarjanSCCTest {

    @Test
    void testSCCWithCycle() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1); // Creates cycle: 1->2->3->1

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCCs(graph);
        List<List<Integer>> components = result.getComponents();

        assertEquals(2, components.size());

        // Find the single-node component and the cycle component
        List<Integer> singleComponent = components.stream()
                .filter(comp -> comp.size() == 1)
                .findFirst()
                .orElse(null);
        List<Integer> cycleComponent = components.stream()
                .filter(comp -> comp.size() == 3)
                .findFirst()
                .orElse(null);

        assertNotNull(singleComponent);
        assertNotNull(cycleComponent);
        assertEquals(0, (int) singleComponent.get(0));
        assertTrue(cycleComponent.contains(1) && cycleComponent.contains(2) && cycleComponent.contains(3));
    }

    @Test
    void testDAGNoSCCs() {
        Graph graph = new Graph(4, true, "edge");
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCCs(graph);
        List<List<Integer>> components = result.getComponents();

        assertEquals(4, components.size()); // Each node is its own component
    }
}