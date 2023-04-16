package br.edu.femass.latteback.dto;

import jakarta.annotation.Nullable;

import lombok.Data;
import java.util.UUID;

@Data
public class ResearcherCacheDto {
    @Nullable
    private UUID id;
    private String name;
    @Nullable
    private String researcheridNumber;
    @Nullable
    private String fileName;    
}
