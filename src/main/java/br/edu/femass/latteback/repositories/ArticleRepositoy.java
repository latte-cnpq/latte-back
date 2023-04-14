package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArticleRepositoy extends JpaRepository<Article, UUID> {
}
