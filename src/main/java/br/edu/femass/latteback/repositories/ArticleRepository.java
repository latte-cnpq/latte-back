package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleCustomRepository {
    List<Article> findByResearcher(Researcher researcher);
    List<Article> findByTitleIgnoreCase(String title);
}
