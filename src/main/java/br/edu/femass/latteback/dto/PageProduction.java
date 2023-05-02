package br.edu.femass.latteback.dto;

import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@Data
public class PageProduction {
    private Page<Book> bookPage;
    private Page<Article> articlePage;
    private Integer page;
    private Integer perPage;
    private String ordination;
    private Sort.Direction direction;
    private Long totalPage;

    public PageProduction() { }

    public PageProduction(Page<Book> bookPage, Page<Article> articlePage, Integer page, Integer perPage) {
        this.bookPage = bookPage;
        this.articlePage = articlePage;
        this.page = page;
        this.perPage = perPage;
        this.ordination = "";
        this.direction = Sort.Direction.ASC;
        this.totalPage = bookPage.getTotalElements() + articlePage.getTotalElements();
    }

    public PageProduction(Page<Book> bookPage, Page<Article> articlePage, Integer page, Integer perPage, String ordination, Sort.Direction direction) {
        this.bookPage = bookPage;
        this.articlePage = articlePage;
        this.page = page;
        this.perPage = perPage;
        this.ordination = ordination;
        this.direction = direction;
    }
}
