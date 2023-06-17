package br.edu.femass.latteback.models.graph;


import br.edu.femass.latteback.models.Researcher;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Data
public class Collaboration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "firstAuthor_fk", foreignKey = @ForeignKey(name = "researcher_pk"), nullable = false)
    private Researcher firstAuthor;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "secondAuthor_fk", foreignKey = @ForeignKey(name = "RESEARCHER_ID"))
    private Researcher secondAuthor;
    @Column(nullable = false, length = 400)
    private String productionTitle;
    @Column(nullable = false)
    private final String productionType;

    public Collaboration() {
        productionType = null;
    }

    public Collaboration(Researcher firstAuthor, Researcher secondAuthor, String productionTitle, Enum productionType) {
        this.productionTitle = productionTitle;
        this.firstAuthor = firstAuthor;
        this.secondAuthor = secondAuthor;
        this.productionType = String.valueOf(productionType);
    }

}
