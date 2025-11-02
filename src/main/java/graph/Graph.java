package graph;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adjacencyList;
    private final boolean directed;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        adjacencyList.get(u).add(new Edge(u, v, weight));
    }

    public List<Edge> getEdges(int u) {
        return adjacencyList.get(u);
    }

    public int getNodeCount() {
        return n;
    }

    public boolean isDirected() {
        return directed;
    }

    public static class Edge {
        public final int u, v, weight;
        public Edge(int u, int v, int weight) {
            this.u = u; this.v = v; this.weight = weight;
        }
    }
}