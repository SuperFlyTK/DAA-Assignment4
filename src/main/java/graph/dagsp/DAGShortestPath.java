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
        TopologicalSort topoSort = new TopologicalSort();
        TopoResult topoResult = topoSort.kahnAlgorithm(graph);

        if (topoResult.hasCycle()) {
            throw new IllegalArgumentException("Graph contains cycles - not a DAG");
        }

        List<Integer> topoOrder = topoResult.getTopologicalOrder();
        int n = graph.getNodeCount();

        // Initialize distances
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        // Relax edges in topological order
        for (int u : topoOrder) {
            metrics.incrementOperation();

            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : graph.getEdges(u)) {
                    int v = edge.v;
                    int weight = edge.weight;

                    metrics.incrementEdgeRelaxations();

                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        prev[v] = u;
                    }
                }
            }
        }

        metrics.stopTimer();

        return new PathResult(dist, prev, source, true, metrics);
    }

    public PathResult findLongestPath(Graph graph, int source) {
        Metrics metrics = new Metrics();
        metrics.startTimer();

        // Convert to shortest path problem by negating weights
        Graph negatedGraph = negateWeights(graph);

        // Find shortest path in negated graph
        PathResult shortestPathResult = findShortestPath(negatedGraph, source);

        // Convert distances back (negate again)
        int[] longestDist = new int[shortestPathResult.getDistances().length];
        for (int i = 0; i < longestDist.length; i++) {
            if (shortestPathResult.getDistances()[i] == Integer.MAX_VALUE) {
                longestDist[i] = Integer.MIN_VALUE;
            } else if (shortestPathResult.getDistances()[i] == Integer.MIN_VALUE) {
                longestDist[i] = Integer.MAX_VALUE;
            } else {
                longestDist[i] = -shortestPathResult.getDistances()[i];
            }
        }

        metrics.stopTimer();

        return new PathResult(longestDist, shortestPathResult.getPredecessors(),
                source, false, metrics);
    }

    public CriticalPathResult findCriticalPath(Graph graph) {
        // For critical path, find the longest path from any source to any sink
        int n = graph.getNodeCount();
        int maxLength = Integer.MIN_VALUE;
        List<Integer> criticalPath = new ArrayList<>();
        PathResult bestResult = null;

        // Try all possible sources
        for (int source = 0; source < n; source++) {
            PathResult longestFromSource = findLongestPath(graph, source);
            int[] dist = longestFromSource.getDistances();

            for (int i = 0; i < n; i++) {
                if (dist[i] != Integer.MIN_VALUE && dist[i] > maxLength) {
                    maxLength = dist[i];
                    bestResult = longestFromSource;
                    criticalPath = longestFromSource.reconstructPath(i);
                }
            }
        }

        return new CriticalPathResult(criticalPath, maxLength, bestResult.getMetrics());
    }

    private Graph negateWeights(Graph graph) {
        Graph negated = new Graph(graph.getNodeCount(), true);

        for (int u = 0; u < graph.getNodeCount(); u++) {
            for (Graph.Edge edge : graph.getEdges(u)) {
                int negatedWeight = (edge.weight == Integer.MIN_VALUE) ?
                        Integer.MAX_VALUE : -edge.weight;
                negated.addEdge(edge.u, edge.v, negatedWeight);
            }
        }

        return negated;
    }
}