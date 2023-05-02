package br.edu.femass.latteback.dto;

import lombok.Data;

@Data
public class ProductionDTO {
    String type;
    String details;

    public ProductionDTO() { }

    public ProductionDTO(String type, String details) {
        this.type = type;
        this.details = details;
    }
}
