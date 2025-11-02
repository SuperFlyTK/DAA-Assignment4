package graph.scc;

import graph.Graph;
import graph.Metrics;

import java.util.*;


public class TarjanSCC {
    private int index;
    private int[] indices;
    private int[] lowlinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> components;
    private Metrics metrics;

    public SCCResult findSCCs(Graph graph) {
        metrics = new Metrics();
        metrics.startTimer();

        int n = graph.getNodeCount();
        indices = new int[n];
        lowlinks = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        components = new ArrayList<>();

        Arrays.fill(indices, -1);
        index = 0;

        for (int i = 0; i < n; i++) {
            if (indices[i] == -1) {
                strongConnect(graph, i);
            }
        }

        metrics.stopTimer();

        // Build condensation graph
        Graph condensationGraph = buildCondensationGraph(graph, components);

        return new SCCResult(components, condensationGraph, metrics);
    }

    private void strongConnect(Graph graph, int v) {
        metrics.incrementDfsVisits();
        indices[v] = index;
        lowlinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;

        for (Graph.Edge edge : graph.getEdges(v)) {
            int w = edge.v;
            metrics.incrementOperation();

            if (indices[w] == -1) {
                strongConnect(graph, w);
                lowlinks[v] = Math.min(lowlinks[v], lowlinks[w]);
            } else if (onStack[w]) {
                lowlinks[v] = Math.min(lowlinks[v], indices[w]);
            }
        }

        if (lowlinks[v] == indices[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != v);
            components.add(component);
        }
    }

    private Graph buildCondensationGraph(Graph originalGraph, List<List<Integer>> components) {
        int componentCount = components.size();
        Graph condensationGraph = new Graph(componentCount, true);

        // Map each node to its component index
        int[] componentMap = new int[originalGraph.getNodeCount()];
        for (int i = 0; i < components.size(); i++) {
            for (int node : components.get(i)) {
                componentMap[node] = i;
            }
        }

        // Add edges between different components
        for (int u = 0; u < originalGraph.getNodeCount(); u++) {
            for (Graph.Edge edge : originalGraph.getEdges(u)) {
                int v = edge.v;
                int compU = componentMap[u];
                int compV = componentMap[v];

                if (compU != compV) {
                    condensationGraph.addEdge(compU, compV, edge.weight);
                }
            }
        }

        return condensationGraph;
    }
}