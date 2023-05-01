package br.edu.femass.latteback.services;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.ProductionInterface;
import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.repositories.ArticleRepositoy;
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
    private final ArticleRepositoy articleRepositoy;


    public ProductionService(BookRepository bookRepository, ArticleRepositoy articleRepositoy) {
        this.bookRepository = bookRepository;
        this.articleRepositoy = articleRepositoy;
    }

    @Override
    public CollectionProduction getAll() {
        List<Book> books = bookRepository.findAll();
        List<Article> articles = articleRepositoy.findAll();

        CollectionProduction productions = new CollectionProduction(books, articles);

        return productions;
    }

    @Override
    public CollectionProduction getAllByResearcher(UUID researcherId) {
        List<Book> books = bookRepository.findByResearcher(researcherId);
        List<Article> articles = articleRepositoy.findByResearcher(researcherId);

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
