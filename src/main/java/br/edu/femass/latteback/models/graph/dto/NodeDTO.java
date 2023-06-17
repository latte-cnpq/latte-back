package br.edu.femass.latteback.models.graph.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NodeDTO {
    private UUID id;
    private String researcherLabel;
    private String instituteLabel;
    public NodeDTO() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getResearcherLabel() {
        return researcherLabel;
    }

    public void setResearcherLabel(String researcherLabel) {
        this.researcherLabel = researcherLabel;
    }

    public String getInstituteLabel() {
        return instituteLabel;
    }

    public void setInstituteLabel(String instituteLabel) {
        this.instituteLabel = instituteLabel;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", id.toString());
        data.put("researcherLabel", researcherLabel);
        data.put("instituteLabel", instituteLabel);
        json.put("data", data);
        return json;
    }
}
