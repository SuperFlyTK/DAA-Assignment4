package graph.io;

import graph.Metrics;
import graph.scc.SCCResult;
import graph.topo.TopoResult;
import graph.dagsp.CriticalPathResult;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CSVReportGenerator {

    public static void generatePerformanceReport(String filename,
                                                 String datasetName,
                                                 int nodes,
                                                 int edges,
                                                 SCCResult sccResult,
                                                 TopoResult topoResult,
                                                 CriticalPathResult criticalResult) throws IOException {

        try (FileWriter writer = new FileWriter(filename)) {
            // Header
            writer.write("Dataset,Nodes,Edges,Algorithm,Time(ns),Operations,DFS_Visits,Edge_Relaxations,Kahn_Operations\n");

            // SCC metrics
            Metrics sccMetrics = sccResult.getMetrics();
            writer.write(String.format("%s,%d,%d,SCC,%d,%d,%d,%d,%d\n",
                    datasetName, nodes, edges,
                    sccMetrics.getElapsedTimeNanos(),
                    sccMetrics.getOperationCount(),
                    sccMetrics.getDfsVisits(),
                    sccMetrics.getEdgeRelaxations(),
                    sccMetrics.getKahnOperations()));

            // Topological Sort metrics
            Metrics topoMetrics = topoResult.getMetrics();
            writer.write(String.format("%s,%d,%d,TopologicalSort,%d,%d,%d,%d,%d\n",
                    datasetName, nodes, edges,
                    topoMetrics.getElapsedTimeNanos(),
                    topoMetrics.getOperationCount(),
                    topoMetrics.getDfsVisits(),
                    topoMetrics.getEdgeRelaxations(),
                    topoMetrics.getKahnOperations()));

            // Critical Path metrics
            Metrics criticalMetrics = criticalResult.getMetrics();
            writer.write(String.format("%s,%d,%d,CriticalPath,%d,%d,%d,%d,%d\n",
                    datasetName, nodes, edges,
                    criticalMetrics.getElapsedTimeNanos(),
                    criticalMetrics.getOperationCount(),
                    criticalMetrics.getDfsVisits(),
                    criticalMetrics.getEdgeRelaxations(),
                    criticalMetrics.getKahnOperations()));
        }
    }

    public static void generateSCCAnalysis(String filename,
                                           List<List<Integer>> components,
                                           int[] componentSizes) throws IOException {

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Component_ID,Size,Nodes\n");

            for (int i = 0; i < components.size(); i++) {
                List<Integer> sortedNodes = new ArrayList<>(components.get(i));
                Collections.sort(sortedNodes);
                writer.write(String.format("%d,%d,%s\n",
                        i,
                        componentSizes[i],
                        sortedNodes.toString()));
            }
        }
    }
}