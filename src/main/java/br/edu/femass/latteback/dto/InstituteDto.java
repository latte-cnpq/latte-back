package br.edu.femass.latteback.dto;

import jakarta.annotation.Nullable;

import java.util.UUID;

public class InstituteDto {
    @Nullable
    private UUID id;
    private String name;
    private String acronym;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }
    //Necessário para testes
    public void setId(UUID id) {
        this.id = id;
    }
}
