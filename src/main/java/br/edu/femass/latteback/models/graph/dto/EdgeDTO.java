package br.edu.femass.latteback.models.graph.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EdgeDTO {

    private UUID source;
    private UUID target;
    private int repetitionCount = 0;
    private String id;
    public EdgeDTO() {
        this.repetitionCount++;
    }

    public UUID getSource() {
        return source;
    }

    public void setSource(UUID source) {
        this.source = source;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public void addRepetitionCount() {
        this.repetitionCount++;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("source", source.toString());
        data.put("target", target.toString());
        data.put("count", repetitionCount);
        json.put("data", data);
        return json;
    }
}
