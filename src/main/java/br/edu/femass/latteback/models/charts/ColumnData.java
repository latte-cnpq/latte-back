package br.edu.femass.latteback.models.charts;

import lombok.Data;

@Data
public class ColumnData {
    private String year;
    private Integer totalBooks;
    private Integer totalArticles;

    public ColumnData() { }

    public ColumnData(String year, Integer totalArticles, Integer totalBooks) {
        this.year = year;
        this.totalArticles = totalArticles;
        this.totalBooks = totalBooks;
    }
}
