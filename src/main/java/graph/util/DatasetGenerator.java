package graph.util;

import graph.Graph;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DatasetGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random(42); // Fixed seed for reproducibility

    public static void main(String[] args) throws IOException {
        DatasetGenerator generator = new DatasetGenerator();

        // Generate all datasets
        generator.generateSmallDatasets();
        generator.generateMediumDatasets();
        generator.generateLargeDatasets();

        System.out.println("All datasets generated successfully!");
    }

    public void generateSmallDatasets() throws IOException {
        // 3 small datasets: 6-10 nodes
        generateDataset("data/small/small_dag_1.json", 8, 12, 0.0, "DAG");
        generateDataset("data/small/small_cyclic_1.json", 7, 10, 0.3, "Cyclic");
        generateDataset("data/small/small_mixed_1.json", 9, 14, 0.2, "Mixed");
    }

    public void generateMediumDatasets() throws IOException {
        // 3 medium datasets: 10-20 nodes
        generateDataset("data/medium/medium_dag_1.json", 15, 25, 0.0, "DAG");
        generateDataset("data/medium/medium_cyclic_1.json", 18, 30, 0.4, "Cyclic");
        generateDataset("data/medium/medium_mixed_1.json", 20, 35, 0.3, "Mixed");
    }

    public void generateLargeDatasets() throws IOException {
        // 3 large datasets: 20-50 nodes
        generateDataset("data/large/large_dag_1.json", 35, 60, 0.0, "DAG");
        generateDataset("data/large/large_cyclic_1.json", 40, 75, 0.5, "Cyclic");
        generateDataset("data/large/large_mixed_1.json", 50, 90, 0.4, "Mixed");
    }

    private void generateDataset(String filename, int nodes, int edges,
                                 double cycleProbability, String type) throws IOException {
        Map<String, Object> graphData = new HashMap<>();
        graphData.put("directed", true);
        graphData.put("n", nodes);
        graphData.put("source", 0);
        graphData.put("weight_model", "edge");

        List<Map<String, Object>> edgeList = new ArrayList<>();
        Set<String> existingEdges = new HashSet<>();

        if (type.equals("DAG")) {
            generateDAG(edgeList, existingEdges, nodes, edges);
        } else {
            generateGraphWithCycles(edgeList, existingEdges, nodes, edges, cycleProbability);
        }

        graphData.put("edges", edgeList);

        // Ensure directory exists
        new File(filename).getParentFile().mkdirs();

        // Write to file
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), graphData);

        System.out.println("Generated: " + filename + " (nodes: " + nodes +
                ", edges: " + edgeList.size() + ", type: " + type + ")");
    }

    private void generateDAG(List<Map<String, Object>> edgeList, Set<String> existingEdges,
                             int nodes, int targetEdges) {
        int edgesGenerated = 0;

        while (edgesGenerated < targetEdges) {
            int u = random.nextInt(nodes - 1);
            int v = u + 1 + random.nextInt(nodes - u - 1);

            String edgeKey = u + "->" + v;
            if (!existingEdges.contains(edgeKey)) {
                addEdge(edgeList, existingEdges, u, v);
                edgesGenerated++;
            }

            // Avoid infinite loop
            if (existingEdges.size() >= (nodes * (nodes - 1)) / 2) {
                break;
            }
        }
    }

    private void generateGraphWithCycles(List<Map<String, Object>> edgeList, Set<String> existingEdges,
                                         int nodes, int targetEdges, double cycleProbability) {
        int edgesGenerated = 0;

        // First create a spanning tree to ensure connectivity
        for (int i = 1; i < nodes; i++) {
            int u = random.nextInt(i);
            addEdge(edgeList, existingEdges, u, i);
            edgesGenerated++;
        }

        // Add remaining edges, allowing cycles
        while (edgesGenerated < targetEdges) {
            int u = random.nextInt(nodes);
            int v = random.nextInt(nodes);

            if (u != v && !existingEdges.contains(u + "->" + v)) {
                // With some probability, allow cycles (v < u for back edges)
                if (u > v && random.nextDouble() < cycleProbability) {
                    addEdge(edgeList, existingEdges, u, v);
                    edgesGenerated++;
                } else if (u < v) {
                    addEdge(edgeList, existingEdges, u, v);
                    edgesGenerated++;
                }
            }

            // Safety break
            if (edgesGenerated >= targetEdges * 1.5) {
                break;
            }
        }
    }

    private void addEdge(List<Map<String, Object>> edgeList, Set<String> existingEdges, int u, int v) {
        String edgeKey = u + "->" + v;
        if (!existingEdges.contains(edgeKey)) {
            Map<String, Object> edge = new HashMap<>();
            edge.put("u", u);
            edge.put("v", v);
            edge.put("w", random.nextInt(10) + 1); // Weight 1-10

            edgeList.add(edge);
            existingEdges.add(edgeKey);
        }
    }
}