package br.edu.femass.latteback.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class ResearcherCache {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    //TODO: private String email; Não encontrado em nenhum XML para ser modelado

    @Column(length = 20)
    private String researcherIdNumber;

    @Column(nullable = false, length = 100)
    private String fileName;

    public ResearcherCache() {
    }

    public ResearcherCache(String name, String researcherIdNumber, String fileName) {
        this.name = name;
        this.researcherIdNumber = researcherIdNumber;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "ResearcherCache [id=" + id + ", name=" + name + ", numeroIdentificador=" + researcherIdNumber
                + "]";
    }
}
