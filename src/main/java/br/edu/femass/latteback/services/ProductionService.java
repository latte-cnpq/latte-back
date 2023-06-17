package br.edu.femass.latteback.services;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.PageProduction;
import br.edu.femass.latteback.dto.ProductionDTO;
import br.edu.femass.latteback.dto.ProductionInterface;
import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.repositories.ArticleRepository;
import br.edu.femass.latteback.repositories.BookRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;
import br.edu.femass.latteback.services.interfaces.ProductionServiceInterface;
import br.edu.femass.latteback.utils.enums.ProductionType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public PageProduction AdvanceSearcher(String title, LocalDate startDate, LocalDate endDate, String researcherName, String instituteName, ProductionType type, Pageable pageable) {
        Page<Article> articlePage = null;
        Page<Book> bookPage = null;
        List<ProductionDTO> productions = new ArrayList<>();

        switch (type) {
            case ARTICLE:
                articlePage = articleRepository.AdvancedSearch(title, startDate, endDate, researcherName, pageable);
                break;
            case BOOK:
                bookPage = bookRepository.AdvancedSearch(title, startDate, endDate, researcherName, pageable);
                break;
            default:
                articlePage = articleRepository.AdvancedSearch(title, startDate, endDate, researcherName, pageable);
                bookPage = bookRepository.AdvancedSearch(title, startDate, endDate, researcherName, pageable);
                break;

        }

        if(articlePage != null) {
            productions.addAll(articlePage.getContent().stream().map(a -> {
                String detail = a.getAuthorNames() + ";" + a.getTitle() + ";" + a.getPublishedOn() + ";" + a.getYear() + ";";
                return new ProductionDTO(ProductionType.ARTICLE.label, detail);
            }).collect(Collectors.toList()));
        }

        if(bookPage != null) {
            productions.addAll(bookPage.getContent().stream().map(a -> {
                String detail = a.getAuthorNames() + ";" + a.getTitle() + ";" + a.getPublisher() + ";" + a.getYear() + ";";
                return new ProductionDTO(ProductionType.BOOK.label, detail);
            }).collect(Collectors.toList()));
        }

        Integer currentPage = pageable.getPageNumber();
        Integer pageSize = productions.size();
        Integer totalPage = 0;

        if(articlePage.getTotalPages() > bookPage.getTotalPages()) {
            totalPage = articlePage.getTotalPages();
        } else if(articlePage.getTotalPages() < bookPage.getTotalPages()) {
            totalPage = bookPage.getTotalPages();
        } else {
            totalPage = articlePage.getTotalPages();
        }


        PageProduction pageProduction = new PageProduction(productions, currentPage, pageSize, totalPage);

        return pageProduction;
    }


}
