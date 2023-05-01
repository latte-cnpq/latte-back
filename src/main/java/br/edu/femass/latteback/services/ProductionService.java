package br.edu.femass.latteback.services;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.ProductionInterface;
import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.repositories.ArticleRepository;
import br.edu.femass.latteback.repositories.BookRepository;
import br.edu.femass.latteback.services.interfaces.ProductionServiceInterface;
import br.edu.femass.latteback.utils.enums.ProductionType;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ProductionService implements ProductionServiceInterface {

    private final BookRepository bookRepository;
    private final ArticleRepository articleRepository;


    public ProductionService(BookRepository bookRepository, ArticleRepository articleRepository) {
        this.bookRepository = bookRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public CollectionProduction getAll() {
        List<Book> books = bookRepository.findAll();
        List<Article> articles = articleRepository.findAll();

        CollectionProduction productions = new CollectionProduction(books, articles);

        return productions;
    }

    @Override
    public CollectionProduction getAllByResearcher(UUID researcherId) {
        List<Book> books = bookRepository.findByResearcher(researcherId);
        List<Article> articles = articleRepository.findByResearcher(researcherId);

        CollectionProduction productions = new CollectionProduction(books, articles);

        return productions;
    }

    @Override
    public ProductionInterface getById(UUID productionId, ProductionType type) {

        return null;
    }


    @Override
    public CollectionProduction AdvanceSearcher(String title, UUID researcherId, UUID instituteId, LocalDate startDate, LocalDate endDate, ProductionType type) {
        return null;
    }
}
