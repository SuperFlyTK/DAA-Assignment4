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
            metrics.incrementOperation(); // process node u
            for (Graph.Edge edge : graph.getEdges(u)) {
                inDegree[edge.v]++;
                metrics.incrementKahnOperations();
                metrics.incrementOperation(); // process edge
            }
        }

        // Initialize queue with nodes having zero in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            metrics.incrementOperation(); // check node i
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementKahnOperations();
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        int visited = 0;

        // Process nodes
        while (!queue.isEmpty()) {
            metrics.incrementOperation(); // while loop
            int u = queue.poll();
            topoOrder.add(u);
            visited++;
            metrics.incrementKahnOperations();

            for (Graph.Edge edge : graph.getEdges(u)) {
                metrics.incrementOperation(); // process edge
                int v = edge.v;
                inDegree[v]--;
                metrics.incrementKahnOperations();

                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementKahnOperations();
                }
            }
        }

        metrics.stopTimer();
        boolean hasCycle = (visited != n);
        return new TopoResult(topoOrder, hasCycle, metrics);
    }
}