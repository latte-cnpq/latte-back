package br.edu.femass.latteback.utils.enums;

public enum ProductionType {
    ALL("Todos"),
    ARTICLE("Artigo pulicado"),
    BOOK("Livro publicado");

    public final String label;

    ProductionType(String label) {
        this.label = label;
    }

}
