package br.edu.femass.latteback.utils.enums;

public enum ResearcherField {
    ALL("all"),
    NAME("name"),
    RESEARCHERIDNUMBER("researcheridNumber");

    public final String label;

    ResearcherField(String label) {
        this.label = label;
    }
}
