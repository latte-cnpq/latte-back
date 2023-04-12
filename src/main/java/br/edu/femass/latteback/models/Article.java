package br.edu.femass.latteback.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String publishedOn;

    @Column(nullable = false)
    private String volume;

    @Column(nullable = false)
    private String pages;

    @Column(nullable = false, length = 4)
    private String year;

    @ElementCollection
    private List<String> authorNames = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "researcher")
    private Researcher researcher;

    public String getAuthorNames() {
        return String.join(", ", authorNames);
    }
}
