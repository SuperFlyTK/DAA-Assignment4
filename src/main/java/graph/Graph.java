package graph;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adjacencyList;
    private final boolean directed;
    private final String weightModel; // "edge" or "node" AS REQUIRED
    private int[] nodeDurations; // For node duration model

    public Graph(int n, boolean directed, String weightModel) {
        this.n = n;
        this.directed = directed;
        this.weightModel = weightModel;
        this.adjacencyList = new ArrayList<>();
        this.nodeDurations = new int[n]; // Initialize with zeros
        for (int i = 0; i < n; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public int getEdgesCount() {
        int count = 0;
        for (List<Edge> edges : adjacencyList) {
            count += edges.size();
        }
        return directed ? count : count / 2;
    }

    // Constructor with node durations
    public Graph(int n, boolean directed, String weightModel, int[] nodeDurations) {
        this(n, directed, weightModel);
        this.nodeDurations = nodeDurations.clone();
    }

    public void addEdge(int u, int v, int weight) {
        adjacencyList.get(u).add(new Edge(u, v, weight));
        if (!directed) {
            adjacencyList.get(v).add(new Edge(v, u, weight));
        }
    }

    // ADD: Method to set node durations
    public void setNodeDurations(int[] durations) {
        if (durations.length != n) {
            throw new IllegalArgumentException("Node durations array must match graph size");
        }
        this.nodeDurations = durations.clone();
    }

    // ADD: Method to get node duration
    public int getNodeDuration(int node) {
        if (node < 0 || node >= n) {
            throw new IllegalArgumentException("Invalid node index");
        }
        return nodeDurations[node];
    }

    // ADD: Method to get all node durations
    public int[] getNodeDurations() {
        return nodeDurations.clone();
    }

    // ADD: Get weight model as required
    public String getWeightModel() {
        return weightModel;
    }

    public List<Edge> getEdges(int u) {
        return Collections.unmodifiableList(adjacencyList.get(u));
    }

    public List<List<Edge>> getAdjacencyList() {
        return Collections.unmodifiableList(adjacencyList);
    }

    public int getNodeCount() {
        return n;
    }

    public boolean isDirected() {
        return directed;
    }

    public Graph transpose() {
        if (!directed) {
            return this;
        }

        Graph transposed = new Graph(n, true, weightModel, nodeDurations);
        for (int u = 0; u < n; u++) {
            for (Edge edge : adjacencyList.get(u)) {
                transposed.addEdge(edge.v, edge.u, edge.weight);
            }
        }
        return transposed;
    }

    public static class Edge {
        public final int u;
        public final int v;
        public final int weight;

        public Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return String.format("(%d -> %d, w=%d)", u, v, weight);
        }
    }
}