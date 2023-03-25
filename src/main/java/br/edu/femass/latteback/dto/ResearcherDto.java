package br.edu.femass.latteback.dto;

import jakarta.annotation.Nullable;

import lombok.Data;
import java.util.UUID;

@Data
public class ResearcherDto {
    @Nullable
    private UUID id;
    private String name;
    @Nullable
    private String email;
    @Nullable
    private String researcheridNumber;
    @Nullable
    private String resume;
    @Nullable
    private UUID instituteID;
    
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setResearcheridNumber(String researcheridNumber) {
        this.researcheridNumber = researcheridNumber;
    }

    public String getResearcheridNumber() {
        return researcheridNumber;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }
    
    public UUID getInstituteID() {
        return instituteID;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
