package br.edu.femass.latteback.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
