package br.edu.femass.latteback.models.graph.dto;

import java.util.List;
import java.util.Map;

public class GraphDataDTO {
    private List<Map<String, Object>> edges;
    private List<Map<String, Object>> nodes;

    public GraphDataDTO() {
    }

    public List<Map<String, Object>> getEdges() {
        return edges;
    }

    public void setEdges(List<Map<String, Object>> edges) {
        this.edges = edges;
    }

    public List<Map<String, Object>> getNodes() {
        return nodes;
    }

    public void setNodes(List<Map<String, Object>> nodes) {
        this.nodes = nodes;
    }
}
