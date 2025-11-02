package graph.topo;

import graph.Graph;
import graph.Metrics;

import java.util.*;

public class TopologicalSort {

    public TopoResult kahnAlgorithm(Graph graph) {
        Metrics metrics = new Metrics();
        metrics.startTimer();

        int n = graph.getNodeCount();
        int[] inDegree = new int[n];

        // Calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getEdges(u)) {
                inDegree[edge.v]++;
                metrics.incrementKahnOperations();
            }
        }

        // Initialize queue with nodes having zero in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        int visited = 0;

        // Process nodes
        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);
            visited++;
            metrics.incrementKahnOperations();

            for (Graph.Edge edge : graph.getEdges(u)) {
                int v = edge.v;
                inDegree[v]--;
                metrics.incrementKahnOperations();

                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        metrics.stopTimer();

        // Check for cycles
        boolean hasCycle = (visited != n);

        return new TopoResult(topoOrder, hasCycle, metrics);
    }
}