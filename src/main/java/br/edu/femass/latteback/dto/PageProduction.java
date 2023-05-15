package br.edu.femass.latteback.dto;

import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class PageProduction {
    private List<ProductionDTO> productions;
    private Integer page;
    private Integer perPage;
    private Integer totalPage;

    public PageProduction() { }

    public PageProduction(List<ProductionDTO> productions, Integer page, Integer totalPage) {
        this.productions = productions;
        this.page = page;
        this.totalPage = totalPage;
    }

    public PageProduction(List<ProductionDTO> productions, Integer page, Integer perPage, Integer totalPage) {
        this.productions = productions;
        this.page = page;
        this.perPage = perPage;
        this.totalPage = totalPage;
    }
}
