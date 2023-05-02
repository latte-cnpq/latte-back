package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ArticleCustomRepository {
    Page<Article> AdvancedSearch(String title, LocalDate startDate, LocalDate endDate, String researcherName, String instituteName, Pageable pageable);
    Page<Article> AdvancedSearch(String title, LocalDate startDate, LocalDate endDate, String researcherName, Pageable pageable);
}
