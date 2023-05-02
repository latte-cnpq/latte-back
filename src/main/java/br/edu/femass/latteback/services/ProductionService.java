package br.edu.femass.latteback.services;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.PageProduction;
import br.edu.femass.latteback.dto.ProductionInterface;
import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.repositories.ArticleRepository;
import br.edu.femass.latteback.repositories.ArticleRepositoy;
import br.edu.femass.latteback.repositories.BookRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;
import br.edu.femass.latteback.services.interfaces.ProductionServiceInterface;
import br.edu.femass.latteback.utils.enums.ProductionType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ProductionService implements ProductionServiceInterface {

    private final BookRepository bookRepository;
    private final ArticleRepository articleRepository;
    private final ResearcherRepository researcherRepository;


    public ProductionService(BookRepository bookRepository, ArticleRepository articleRepository, ResearcherRepository researcherRepository) {
        this.bookRepository = bookRepository;
        this.articleRepository = articleRepository;
        this.researcherRepository = researcherRepository;
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

        Researcher researcher = researcherRepository.findById(researcherId).get();

        List<Book> books = bookRepository.findByResearcher(researcher);
        List<Article> articles = articleRepository.findByResearcher(researcher);

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
                production = articleRepository.findById(productionId).get();
                break;
            default:
                production = null;
                break;
        }

        return production;
    }

    @Override
    public PageProduction AdvanceSearcher(String title, LocalDate startDate, LocalDate endDate, UUID researcherId, UUID instituteId, Pageable pageable) {
        Integer currentPage = 0;

        Page<Article> articlePage = articleRepository.AdvancedSearch(title, startDate, endDate, researcherId, pageable);
        Page<Book> bookPage = bookRepository.AdvancedSearch(title, startDate, endDate, researcherId, pageable);

        final Integer bookCurrentPage = bookPage.getPageable().getPageNumber();
        final Integer articleCurrentPage = articlePage.getPageable().getPageNumber();
        final Integer pageSize = articlePage.getPageable().getPageSize();

        if(bookCurrentPage > articleCurrentPage) {
            currentPage = bookCurrentPage;
        } else if (bookCurrentPage < articleCurrentPage) {
            currentPage = articleCurrentPage;
        } else {
            currentPage = articleCurrentPage;
        }

        PageProduction pageProduction = new PageProduction(bookPage, articlePage, currentPage, pageSize);

        return pageProduction;
    }


}
