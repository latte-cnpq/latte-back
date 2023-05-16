package br.edu.femass.latteback.services.interfaces;

import br.edu.femass.latteback.models.charts.CircleData;
import br.edu.femass.latteback.models.charts.ColumnData;
import br.edu.femass.latteback.models.charts.CounterData;
import br.edu.femass.latteback.utils.enums.ProductionType;

import java.util.List;

public interface DataServiceInterface {
    List<ColumnData> getAllColumnData();

    ColumnData getByYear(Integer year);

    List<ColumnData> getByYearRange(Integer startYear, Integer endYear);

    List<ColumnData> getColumnDataByFilter(Integer startYear, Integer endYear, String researcherName, String instituteName, ProductionType type);

    CircleData getAllCircleData();

    CounterData getCounterData();

}
