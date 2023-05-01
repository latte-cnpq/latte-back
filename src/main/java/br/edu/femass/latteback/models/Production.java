package br.edu.femass.latteback.models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Production {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @Column(nullable = false)
    private String productionType;
    @Column(nullable = false)
    private String productionDetails;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ElementCollection
    @JoinColumn(name = "production_author_names", foreignKey = @ForeignKey(name = "production_author_names_fk"))
    private List<String> authorNames = new ArrayList<>();
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "researcher", foreignKey = @ForeignKey(name = "research_fk"))
    private Researcher researcher;
    @JoinColumn(name = "article", foreignKey = @ForeignKey(name = "article_fk"))
    private Article article;
    @JoinColumn(name = "book", foreignKey = @ForeignKey(name = "book_fk"))
    private Book book;
}

public Production() {

}

public Production(String productionType, String productionDetails, List<String> authorNames, Researcher researcher, Article article, Book book) {
    this.productionType = productionType;
    this.productionDetails = productionDetails;
    this.authorNames = authorNames;
    this.researcher = researcher;
    this.article = article;
    this.book = book;
}

public String getAuthorNames() {
    return String.join(", ", authorNames);
}
