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

        sortComponents();

        // Build condensation graph
        Graph condensationGraph = buildCondensationGraph(graph, components);

        return new SCCResult(components, condensationGraph, metrics);
    }

    private void strongConnect(Graph graph, int v) {
        metrics.incrementDfsVisits();
        metrics.incrementOperation(); // node visit
        indices[v] = index;
        lowlinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;
        metrics.incrementOperation(); // stack push

        for (Graph.Edge edge : graph.getEdges(v)) {
            int w = edge.v;
            metrics.incrementOperation(); // process edge
            metrics.incrementEdgeRelaxations(); // COUNT AS EDGE RELAXATION FOR SCC

            if (indices[w] == -1) {
                strongConnect(graph, w);
                lowlinks[v] = Math.min(lowlinks[v], lowlinks[w]);
                metrics.incrementOperation(); // update lowlink
            } else if (onStack[w]) {
                lowlinks[v] = Math.min(lowlinks[v], indices[w]);
                metrics.incrementOperation(); // found in stack
            }
        }

        if (lowlinks[v] == indices[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
                metrics.incrementOperation(); // pop from stack
            } while (w != v);

            Collections.sort(component);
            components.add(component);
        }
    }

    private void sortComponents() {
        for (List<Integer> component : components) {
            Collections.sort(component);
        }

        components.sort((c1, c2) -> {
            int first1 = c1.get(0);
            int first2 = c2.get(0);
            return Integer.compare(first1, first2);
        });
    }

    private Graph buildCondensationGraph(Graph originalGraph, List<List<Integer>> components) {
        int componentCount = components.size();
        Graph condensationGraph = new Graph(componentCount, true, originalGraph.getWeightModel());

        // Map each node to its component index
        int[] componentMap = new int[originalGraph.getNodeCount()];
        for (int i = 0; i < components.size(); i++) {
            for (int node : components.get(i)) {
                componentMap[node] = i;
            }
        }

        // Add edges between different components
        Set<String> addedEdges = new HashSet<>();
        for (int u = 0; u < originalGraph.getNodeCount(); u++) {
            for (Graph.Edge edge : originalGraph.getEdges(u)) {
                metrics.incrementOperation(); // process edge for condensation
                int v = edge.v;
                int compU = componentMap[u];
                int compV = componentMap[v];

                if (compU != compV) {
                    String edgeKey = compU + "->" + compV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensationGraph.addEdge(compU, compV, edge.weight);
                        addedEdges.add(edgeKey);
                        metrics.incrementOperation(); // add condensation edge
                    }
                }
            }
        }

        return condensationGraph;
    }
}