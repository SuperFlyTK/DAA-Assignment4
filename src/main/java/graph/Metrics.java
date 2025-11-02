package graph;

public class Metrics {
    private long startTime;
    private long endTime;
    private int operationCount;
    private int dfsVisits;
    private int edgeRelaxations;
    private int kahnOperations;

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    public void incrementOperation() { operationCount++; }
    public void incrementDfsVisits() { dfsVisits++; }
    public void incrementEdgeRelaxations() { edgeRelaxations++; }
    public void incrementKahnOperations() { kahnOperations++; }

    // Getters
    public int getOperationCount() { return operationCount; }
    public int getDfsVisits() { return dfsVisits; }
    public int getEdgeRelaxations() { return edgeRelaxations; }
    public int getKahnOperations() { return kahnOperations; }

    public void reset() {
        operationCount = dfsVisits = edgeRelaxations = kahnOperations = 0;
        startTime = endTime = 0;
    }
}