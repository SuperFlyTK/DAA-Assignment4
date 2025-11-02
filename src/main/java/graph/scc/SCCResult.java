package graph.scc;

import graph.Graph;
import graph.Metrics;

import java.util.List;


public class SCCResult {
    private final List<List<Integer>> components;
    private final Graph condensationGraph;
    private final Metrics metrics;

    public SCCResult(List<List<Integer>> components, Graph condensationGraph, Metrics metrics) {
        this.components = components;
        this.condensationGraph = condensationGraph;
        this.metrics = metrics;
    }

    public List<List<Integer>> getComponents() {
        return components;
    }

    public Graph getCondensationGraph() {
        return condensationGraph;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    // ADD METHOD TO GET SCC SIZES AS REQUIRED
    public int[] getComponentSizes() {
        int[] sizes = new int[components.size()];
        for (int i = 0; i < components.size(); i++) {
            sizes[i] = components.get(i).size();
        }
        return sizes;
    }

    public void printResults() {
        System.out.println("Strongly Connected Components:");
        System.out.println("Found " + components.size() + " components");

        for (int i = 0; i < components.size(); i++) {
            System.out.println("Component " + i + " (size: " + components.get(i).size() + "): " + components.get(i));
        }

        System.out.println("\nCondensation Graph:");
        System.out.println("Nodes: " + condensationGraph.getNodeCount());
        for (int i = 0; i < condensationGraph.getNodeCount(); i++) {
            System.out.println("  Node " + i + " -> " + condensationGraph.getEdges(i));
        }

        System.out.println("\n" + metrics);
    }
}