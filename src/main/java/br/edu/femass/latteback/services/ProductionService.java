package br.edu.femass.latteback.services;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.PageProduction;
import br.edu.femass.latteback.dto.ProductionInterface;
import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.repositories.ArticleRepositoy;
import br.edu.femass.latteback.repositories.BookRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;
import br.edu.femass.latteback.services.interfaces.ProductionServiceInterface;
import br.edu.femass.latteback.utils.enums.ProductionType;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ProductionService implements ProductionServiceInterface {

    private final BookRepository bookRepository;
    private final ArticleRepositoy articleRepositoy;
    private final ResearcherRepository researcherRepository;


    public ProductionService(BookRepository bookRepository, ArticleRepositoy articleRepositoy, ResearcherRepository researcherRepository) {
        this.bookRepository = bookRepository;
        this.articleRepositoy = articleRepositoy;
        this.researcherRepository = researcherRepository;
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

        Researcher researcher = researcherRepository.findById(researcherId).get();

        List<Book> books = bookRepository.findByResearcher(researcher);
        List<Article> articles = articleRepositoy.findByResearcher(researcher);

        CollectionProduction productions = new CollectionProduction(books, articles);

        return productions;
    }

    @Override
    public ProductionInterface getById(UUID productionId, ProductionType type) {
        ProductionInterface production;

        switch (type) {
            case BOOK:
                production = bookRepository.findById(productionId).get();
                break;
            case ARTICLE:
                production = articleRepositoy.findById(productionId).get();
                break;
            default:
                production = null;
                break;
        }

        return production;
    }


    @Override
    public PageProduction AdvanceSearcher(String title, UUID researcherId, UUID instituteId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return null;
    }
}
