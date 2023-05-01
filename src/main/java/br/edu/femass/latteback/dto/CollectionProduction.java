package br.edu.femass.latteback.dto;

import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class CollectionProduction {
    @Getter
    @Setter
    private List<Book> books = new ArrayList<>();
    @Getter
    @Setter
    private List<Article> articles = new ArrayList<>();
    @Getter
    private final Integer totalProductions = books.size() + articles.size();

    public  CollectionProduction() { }

    public CollectionProduction(List<Book> books, List<Article> articles) {
        this.books = books;
        this.articles = articles;
    }
}
