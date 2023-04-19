package br.edu.femass.latteback.models;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ElementCollection
    @Column (length = 400)
    @JoinColumn(name = "book_author_names", foreignKey = @ForeignKey(name = "book_author_names_fk"))
    private List<String> authorNames = new ArrayList<>();
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "researcher", foreignKey = @ForeignKey(name = "research_fk"))
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