package br.edu.femass.latteback.models.charts;

import lombok.Data;

@Data
public class CounterData {
    private Long totalInstitutes;
    private Long totalResearchers;

    public CounterData() { }

    public CounterData(Long totalInstitutes, Long totalResearchers) {
        this.totalInstitutes = totalInstitutes;
        this.totalResearchers = totalResearchers;
    }

}
