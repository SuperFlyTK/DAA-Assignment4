package graph.dagsp;

import graph.Graph;
import graph.Metrics;
import graph.topo.TopologicalSort;
import graph.topo.TopoResult;

import java.util.*;

public class DAGShortestPath {

    public PathResult findShortestPath(Graph graph, int source) {
        Metrics metrics = new Metrics();
        metrics.startTimer();

        // First get topological order
        TopologicalSort topo = new TopologicalSort();
        TopoResult topoResult = topo.kahnAlgorithm(graph);

        if (topoResult.hasCycle()) {
            throw new IllegalArgumentException("Graph contains cycles - cannot compute shortest paths in cyclic graph");
        }

        List<Integer> topologicalOrder = topoResult.getTopologicalOrder();
        metrics.addAll(topoResult.getMetrics());

        int n = graph.getNodeCount();
        int[] distances = new int[n];
        int[] predecessors = new int[n];

        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;

        // Process nodes in topological order
        for (int u : topologicalOrder) {
            metrics.incrementOperation();

            if (distances[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : graph.getEdges(u)) {
                    int v = edge.v;
                    int weight = edge.weight;
                    metrics.incrementOperation();
                    metrics.incrementEdgeRelaxations();

                    if (distances[u] + weight < distances[v]) {
                        distances[v] = distances[u] + weight;
                        predecessors[v] = u;
                        metrics.incrementOperation();
                    }
                }
            }
        }

        metrics.stopTimer();
        return new PathResult(distances, predecessors, source, true, metrics);
    }

    public PathResult findLongestPath(Graph graph, int source) {
        Metrics metrics = new Metrics();
        metrics.startTimer();

        // For longest path, we process in topological order with max
        TopologicalSort topo = new TopologicalSort();
        TopoResult topoResult = topo.kahnAlgorithm(graph);

        if (topoResult.hasCycle()) {
            throw new IllegalArgumentException("Graph contains cycles - cannot compute longest paths in cyclic graph");
        }

        List<Integer> topologicalOrder = topoResult.getTopologicalOrder();
        metrics.addAll(topoResult.getMetrics());

        int n = graph.getNodeCount();
        int[] distances = new int[n];
        int[] predecessors = new int[n];

        Arrays.fill(distances, Integer.MIN_VALUE);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;

        // Process nodes in topological order for LONGEST path
        for (int u : topologicalOrder) {
            metrics.incrementOperation();

            if (distances[u] != Integer.MIN_VALUE) {
                for (Graph.Edge edge : graph.getEdges(u)) {
                    int v = edge.v;
                    int weight = edge.weight;
                    metrics.incrementOperation();
                    metrics.incrementEdgeRelaxations();

                    if (distances[u] + weight > distances[v]) {
                        distances[v] = distances[u] + weight;
                        predecessors[v] = u;
                        metrics.incrementOperation();
                    }
                }
            }
        }

        metrics.stopTimer();
        return new PathResult(distances, predecessors, source, false, metrics);
    }

    public CriticalPathResult findCriticalPath(Graph graph) {
        Metrics metrics = new Metrics();
        metrics.startTimer();

        TopologicalSort topo = new TopologicalSort();
        TopoResult topoResult = topo.kahnAlgorithm(graph);

        if (topoResult.hasCycle()) {
            throw new IllegalArgumentException("Graph contains cycles - cannot compute critical path");
        }

        List<Integer> topologicalOrder = topoResult.getTopologicalOrder();
        metrics.addAll(topoResult.getMetrics());

        int n = graph.getNodeCount();
        int[] dist = new int[n];
        int[] pred = new int[n];

        Arrays.fill(dist, 0); // Start with 0 for all nodes
        Arrays.fill(pred, -1);

        // Standard longest path in DAG algorithm
        for (int u : topologicalOrder) {
            metrics.incrementOperation();

            for (Graph.Edge edge : graph.getEdges(u)) {
                int v = edge.v;
                int weight = edge.weight;
                metrics.incrementOperation();
                metrics.incrementEdgeRelaxations();

                if (dist[u] + weight > dist[v]) {
                    dist[v] = dist[u] + weight;
                    pred[v] = u;
                    metrics.incrementOperation();
                }
            }
        }

        // Find the node with maximum distance
        int maxDist = 0;
        int endNode = 0;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                endNode = i;
            }
        }

        // Reconstruct the critical path
        List<Integer> criticalPath = new ArrayList<>();
        int current = endNode;
        while (current != -1) {
            criticalPath.add(0, current);
            current = pred[current];
        }

        // If no path found (graph with no edges), return single node
        if (criticalPath.isEmpty() && n > 0) {
            criticalPath.add(0);
        }

        metrics.stopTimer();
        return new CriticalPathResult(criticalPath, maxDist, metrics);
    }
}