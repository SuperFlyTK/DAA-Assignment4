package graph.dagsp;

import graph.Metrics;

import java.util.List;


public class CriticalPathResult {
    private final List<Integer> criticalPath;
    private final int length;
    private final Metrics metrics;

    public CriticalPathResult(List<Integer> criticalPath, int length, Metrics metrics) {
        this.criticalPath = criticalPath;
        this.length = length;
        this.metrics = metrics;
    }

    public List<Integer> getCriticalPath() {
        return criticalPath;
    }

    public int getLength() {
        return length;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}