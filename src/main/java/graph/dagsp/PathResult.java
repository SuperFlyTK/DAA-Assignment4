package graph.dagsp;

import graph.Metrics;

import java.util.ArrayList;
import java.util.List;

/**
 * Result container for path algorithms
 */
public class PathResult {
    private final int[] distances;
    private final int[] predecessors;
    private final int source;
    private final boolean isShortest;
    private final Metrics metrics;

    public PathResult(int[] distances, int[] predecessors, int source,
                      boolean isShortest, Metrics metrics) {
        this.distances = distances.clone();
        this.predecessors = predecessors.clone();
        this.source = source;
        this.isShortest = isShortest;
        this.metrics = metrics;
    }

    public int[] getDistances() {
        return distances.clone();
    }

    public int[] getPredecessors() {
        return predecessors.clone();
    }

    public int getSource() {
        return source;
    }

    public boolean isShortest() {
        return isShortest;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public List<Integer> reconstructPath(int target) {
        if (predecessors[target] == -1 && target != source) {
            return new ArrayList<>(); // No path exists
        }

        List<Integer> path = new ArrayList<>();
        int current = target;

        // Backtrack from target to source
        while (current != -1) {
            path.add(0, current);
            current = predecessors[current];
        }

        // Check if we reached the source
        if (path.get(0) != source) {
            return new ArrayList<>(); // No path exists
        }

        return path;
    }
}