package br.edu.femass.latteback.services.interfaces;

import br.edu.femass.latteback.models.charts.CircleData;
import br.edu.femass.latteback.models.charts.ColumnData;

import java.util.List;

public interface DataServiceInterface {
    List<ColumnData> getAllColumnData();
    ColumnData getByYear(Integer year);
    List<ColumnData> getByYearRange(Integer startYear, Integer endYear);
    CircleData getAllCircleData();
}
