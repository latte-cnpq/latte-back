package br.edu.femass.latteback.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ResearcherFormDTO {
    private String researcherIdNumber;
    private UUID instituteId;
}
