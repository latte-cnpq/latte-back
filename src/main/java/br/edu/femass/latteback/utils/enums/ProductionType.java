package br.edu.femass.latteback.utils.enums;

public enum ProductionType {
    ARTICLE("Article"),
    BOOK("Book");

    public final String label;

    ProductionType(String label) {
        this.label = label;
    }
}
