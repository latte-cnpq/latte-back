package br.edu.femass.latteback.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Column(nullable = false)
    private String researcheridNumber;

    @Column(length = 5000)
    private String resume;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institute_id")
    private Institute institute;

    public Researcher() {
    }

    public Researcher(String name, String researcheridNumber, String resume) {
        this.name = name;
        this.researcheridNumber = researcheridNumber;
        this.resume = resume;
    }

}
