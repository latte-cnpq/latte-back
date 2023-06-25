package br.edu.femass.latteback.models.graph.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NodeDTO {
    private UUID id;
    private String label;
    private int count = 0;
    private UUID instituteId;

    public NodeDTO() {
    }

    public NodeDTO(UUID id, String label) {
        this.id = id;
        this.label = label;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setInstituteId(UUID instituteId) {
        this.instituteId = instituteId;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public void addCount() {
        this.count++;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", id.toString());
        data.put("label", label);
        data.put("count", count);
        if (instituteId != null)
            data.put("instituteId", instituteId);
        json.put("data", data);
        return json;
    }
}
