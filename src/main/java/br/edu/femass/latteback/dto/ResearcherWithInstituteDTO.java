package br.edu.femass.latteback.dto;

import br.edu.femass.latteback.models.Institute;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
public class ResearcherWithInstituteDTO {
    private UUID id;
    private String name;
    private String email;
    private String researcheridNumber;
    private String resume;
    private UUID instituteId;
    private String instituteName;
    private String instituteAcronym;


}
