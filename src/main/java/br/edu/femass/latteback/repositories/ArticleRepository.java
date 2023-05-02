package br.edu.femass.latteback.repositories;
import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.utils.enums.ProductionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    List<Article> findByResearcher(UUID researcherId);
    List<Article> findByInstitute(UUID instituteId);
    List<Article> findByCriteria(String title, UUID researcherId, UUID instituteId, LocalDate startDate, LocalDate endDate, ProductionType type);
}
