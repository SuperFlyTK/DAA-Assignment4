package graph.topo;

import graph.Metrics;

import java.util.List;

public class TopoResult {
    private final List<Integer> topologicalOrder;
    private final boolean hasCycle;
    private final Metrics metrics;

    public TopoResult(List<Integer> topologicalOrder, boolean hasCycle, Metrics metrics) {
        this.topologicalOrder = topologicalOrder;
        this.hasCycle = hasCycle;
        this.metrics = metrics;
    }

    public List<Integer> getTopologicalOrder() {
        return topologicalOrder;
    }

    public boolean hasCycle() {
        return hasCycle;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}