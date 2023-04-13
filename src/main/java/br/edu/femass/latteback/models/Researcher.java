package br.edu.femass.latteback.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;


@Entity
@Data
public class Researcher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String email;

    @Column(nullable = false, length = 30)
    private String researcheridNumber;
    
    @Column(length = 2000)
    private String resume;
    private UUID instituteID;


    public Researcher() {
    }

    public Researcher(String name, String researcheridNumber, String resume) {
        this.name = name;
        this.researcheridNumber = researcheridNumber;
        this.resume = resume;
    }

}
