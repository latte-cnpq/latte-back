package br.edu.femass.latteback.models;

import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data; 

@Entity
@Data
public class ResearcherCache {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, length = 100)
    private String name;
    //TODO: private String email; Não encontrado em nenhum XML para ser modelado
    
    @Column(length = 20)
    private String researcheridNumber;

    @Column(nullable = false, length = 100)
    private String fileName;
    @Override
    public String toString() {
        return "ResearcherCache [id=" + id + ", name=" + name + ", numeroIdentificador=" + researcheridNumber
                + "]";
    }

    public ResearcherCache() {
    }

    public ResearcherCache(String name, String researcheridNumber, String fileName) {
        this.name = name;
        this.researcheridNumber = researcheridNumber;
        this.fileName = fileName;
    }
}
