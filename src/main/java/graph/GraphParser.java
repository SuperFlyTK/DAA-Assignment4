package graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class GraphParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Graph parseFromJson(String filePath) throws IOException {
        Map<String, Object> graphData = mapper.readValue(
                new File(filePath),
                new TypeReference<Map<String, Object>>() {}
        );

        boolean directed = (Boolean) graphData.get("directed");
        int n = (Integer) graphData.get("n");
        List<Map<String, Object>> edges = (List<Map<String, Object>>) graphData.get("edges");

        Graph graph = new Graph(n, directed);

        for (Map<String, Object> edge : edges) {
            int u = (Integer) edge.get("u");
            int v = (Integer) edge.get("v");
            int w = (Integer) edge.get("w");
            graph.addEdge(u, v, w);
        }

        return graph;
    }
}