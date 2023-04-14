package br.edu.femass.latteback.models;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String publisher;
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

    public Book() {
    }

    public Book(String title, String publishedOn, String volume, String pages, String year, List<String> authorNames, Researcher researcher) {
        this.title = title;
        this.publisher = publishedOn;
        this.volume = volume;
        this.pages = pages;
        this.year = year;
        this.authorNames = authorNames;
        this.researcher = researcher;
    }

    public String getAuthorNames() {
        return String.join(", ", authorNames);
    }

}