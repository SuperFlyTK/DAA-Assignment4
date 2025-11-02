package graph.scc;

import graph.Graph;
import graph.GraphParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TarjanSCCTest {

    @Test
    void testSCCWithCycle() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1); // Creates cycle: 1->2->3->1

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCCs(graph);
        List<List<Integer>> components = result.getComponents();

        assertEquals(2, components.size());
        assertTrue(components.stream().anyMatch(comp -> comp.contains(0) && comp.size() == 1));
        assertTrue(components.stream().anyMatch(comp -> comp.contains(1) && comp.contains(2) && comp.contains(3)));
    }

    @Test
    void testDAGNoSCCs() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        TarjanSCC tarjan = new TarjanSCC();
        SCCResult result = tarjan.findSCCs(graph);
        List<List<Integer>> components = result.getComponents();

        assertEquals(4, components.size()); // Each node is its own component
    }
}