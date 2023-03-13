package br.edu.femass.latteback.utils.enums;

public enum InstituteField {
    ALL("all"),
    NAME("name"),
    ACRONYM("acronym");

    public final String label;

    InstituteField(String label) {
        this.label = label;
    }
}
