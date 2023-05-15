package br.edu.femass.latteback.models.charts;

import lombok.Data;

@Data
public class CircleData {
    private Integer totalBooks;
    private Integer totalArticles;

    public CircleData() { }

    public CircleData(Integer totalArticles, Integer totalBooks) {
        this.totalArticles = totalArticles;
        this.totalBooks = totalBooks;
    }
}
