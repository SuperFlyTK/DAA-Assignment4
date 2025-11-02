package graph;

import graph.dagsp.CriticalPathResult;
import graph.dagsp.DAGShortestPath;
import graph.dagsp.PathResult;
import graph.io.CSVReportGenerator;
import graph.scc.SCCResult;
import graph.scc.TarjanSCC;
import graph.topo.TopoResult;
import graph.topo.TopologicalSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // ИСПОЛЬЗУЕМ tasks.json КАК ОСНОВНОЙ ВХОД
        String inputFile = "tasks.json";
        if (args.length >= 1) {
            inputFile = args[0];
        }

        try {
            System.out.println("=== Smart Campus Scheduling - Assignment 4 ===");
            System.out.println("Processing: " + inputFile);

            // Parse graph
            Graph graph = GraphParser.parseFromJson(inputFile);
            int source = getSourceFromJson(inputFile);

            System.out.println("Graph loaded: " + graph.getNodeCount() + " nodes, " +
                    getEdgesCount(graph) + " edges, directed: " + graph.isDirected());
            System.out.println("Weight model: " + graph.getWeightModel());
            System.out.println("Source node: " + source);

            // 1. SCC Detection
            System.out.println("\n--- 1. Strongly Connected Components ---");
            TarjanSCC tarjan = new TarjanSCC();
            SCCResult sccResult = tarjan.findSCCs(graph);

            // OUTPUT SCC SIZES AS REQUIRED
            List<List<Integer>> components = sccResult.getComponents();
            System.out.println("Found " + components.size() + " components with sizes:");
            for (int i = 0; i < components.size(); i++) {
                // СОРТИРУЕМ УЗЛЫ ДЛЯ ЧИТАЕМОСТИ
                List<Integer> sortedComponent = new ArrayList<>(components.get(i));
                Collections.sort(sortedComponent);
                System.out.println("  Component " + i + ": " + sortedComponent.size() + " nodes - " + sortedComponent);
            }

            // 2. Topological Sort on Condensation Graph
            System.out.println("\n--- 2. Topological Sort ---");
            Graph condensationGraph = sccResult.getCondensationGraph();
            TopologicalSort topo = new TopologicalSort();
            TopoResult topoResult = topo.kahnAlgorithm(condensationGraph);

            if (!topoResult.hasCycle()) {
                System.out.println("Topological Order of Components: " + topoResult.getTopologicalOrder());

                // OUTPUT DERIVED ORDER OF ORIGINAL TASKS AS REQUIRED
                List<Integer> originalTaskOrder = deriveOriginalTaskOrder(
                        topoResult.getTopologicalOrder(), components);
                System.out.println("Derived Order of Original Tasks: " + originalTaskOrder);
            } else {
                System.out.println("Graph contains cycles - cannot perform topological sort");
            }

            // 3. Shortest and Longest Paths
            System.out.println("\n--- 3. Shortest and Longest Paths ---");
            DAGShortestPath dagSP = new DAGShortestPath();

            // Use source from JSON or default to 0
            int sourceComponent = findSourceComponent(source, components);
            System.out.println("Source node " + source + " belongs to component: " + sourceComponent);

            if (!topoResult.hasCycle()) {
                System.out.println("Using condensation graph (DAG) for shortest paths...");

                // SHORTEST PATHS НА CONDENSATION GRAPH
                PathResult shortestResult = dagSP.findShortestPath(condensationGraph, sourceComponent);
                System.out.println("Shortest distances from source component " + sourceComponent + ":");
                int[] distances = shortestResult.getDistances();
                for (int i = 0; i < distances.length; i++) {
                    if (distances[i] == Integer.MAX_VALUE) {
                        System.out.println("  Component " + i + ": unreachable");
                    } else {
                        System.out.println("  Component " + i + ": " + distances[i]);
                        // RECONSTRUCT OPTIMAL PATH МЕЖДУ КОМПОНЕНТАМИ
                        List<Integer> path = shortestResult.reconstructPath(i);
                        if (!path.isEmpty() && path.get(0) == sourceComponent && i != sourceComponent) {
                            System.out.println("    Component path: " + path);
                            // ПОКАЗЫВАЕМ СООТВЕТСТВУЮЩИЕ ОРИГИНАЛЬНЫЕ НОДЫ
                            System.out.println("    Original nodes path: " + getOriginalNodesPath(path, components));
                        }
                    }
                }

                // CRITICAL PATH НА CONDENSATION GRAPH
                CriticalPathResult criticalResult = dagSP.findCriticalPath(condensationGraph);
                System.out.println("Critical Path Length: " + criticalResult.getLength());
                System.out.println("Critical Path (components): " + criticalResult.getCriticalPath());
                System.out.println("Critical Path (original nodes): " + getOriginalNodesPath(criticalResult.getCriticalPath(), components));

                // 4. GENERATE CSV REPORTS
                System.out.println("\n--- 4. Generating Analysis Reports ---");
                generateCSVReports(inputFile, graph, sccResult, topoResult, criticalResult);
            } else {
                System.out.println("Original graph contains cycles - using condensation graph for DAG algorithms");
                System.out.println("Note: Shortest paths are computed between SCC components, not original nodes");
            }

            System.out.println("\n=== Analysis Complete ===");

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int getSourceFromJson(String filePath) {
        try {
            java.util.Map<String, Object> graphData = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(new java.io.File(filePath),
                            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
            return (Integer) graphData.get("source");
        } catch (Exception e) {
            return 0;
        }
    }

    private static int getEdgesCount(Graph graph) {
        int count = 0;
        for (int i = 0; i < graph.getNodeCount(); i++) {
            count += graph.getEdges(i).size();
        }
        return graph.isDirected() ? count : count / 2;
    }

    private static List<Integer> deriveOriginalTaskOrder(List<Integer> componentOrder,
                                                         List<List<Integer>> components) {
        List<Integer> originalOrder = new ArrayList<>();
        for (int compId : componentOrder) {
            // СОРТИРУЕМ УЗЛЫ ВНУТРИ КОМПОНЕНТА ДЛЯ ЧИТАЕМОСТИ
            List<Integer> sortedNodes = new ArrayList<>(components.get(compId));
            Collections.sort(sortedNodes);
            originalOrder.addAll(sortedNodes);
        }
        return originalOrder;
    }

    /**
     * Find which component contains the source node
     */
    private static int findSourceComponent(int sourceNode, List<List<Integer>> components) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).contains(sourceNode)) {
                System.out.println("Debug: Source node " + sourceNode + " found in component " + i + ": " + components.get(i));
                return i;
            }
        }
        return 0; // fallback
    }

    /**
     * Convert component path to original nodes path
     */
    private static List<Integer> getOriginalNodesPath(List<Integer> componentPath, List<List<Integer>> components) {
        List<Integer> originalPath = new ArrayList<>();
        if (componentPath.isEmpty()) {
            return originalPath;
        }

        for (int compId : componentPath) {
            if (!components.get(compId).isEmpty()) {
                List<Integer> sortedNodes = new ArrayList<>(components.get(compId));
                Collections.sort(sortedNodes);
                originalPath.add(sortedNodes.get(0));
            }
        }
        return originalPath;
    }

    private static void generateCSVReports(String inputFile, Graph graph,
                                           SCCResult sccResult, TopoResult topoResult,
                                           CriticalPathResult criticalResult) {
        try {
            // Create reports directory
            new java.io.File("reports").mkdirs();

            String baseName = inputFile.replace(".json", "").replace("data/", "");
            if (baseName.equals("tasks")) {
                baseName = "analysis";
            }

            // 1. Performance report
            CSVReportGenerator.generatePerformanceReport(
                    "reports/" + baseName + "_performance.csv",
                    inputFile,
                    graph.getNodeCount(),
                    getEdgesCount(graph),
                    sccResult,
                    topoResult,
                    criticalResult
            );

            // 2. SCC analysis report
            CSVReportGenerator.generateSCCAnalysis(
                    "reports/" + baseName + "_scc.csv",
                    sccResult.getComponents(),
                    sccResult.getComponentSizes()
            );

            System.out.println("CSV reports generated in 'reports/' folder:");
            System.out.println("  - " + baseName + "_performance.csv");
            System.out.println("  - " + baseName + "_scc.csv");

        } catch (Exception e) {
            System.err.println("Error generating CSV reports: " + e.getMessage());
        }
    }
}