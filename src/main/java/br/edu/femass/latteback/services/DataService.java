package br.edu.femass.latteback.services;

import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.models.charts.CircleData;
import br.edu.femass.latteback.models.charts.ColumnData;
import br.edu.femass.latteback.services.interfaces.DataServiceInterface;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DataService implements DataServiceInterface {

    private final ProductionService productionService;

    public DataService(ProductionService productionService) {
        this.productionService = productionService;
    }

    @Override
    public List<ColumnData> getAllColumnData() {
        List<ColumnData> datas = new ArrayList<>();
        List<String> currentYears = new ArrayList<>();

        CollectionProduction productions = productionService.getAll();

        currentYears.addAll(productions.getArticles().stream().map(article -> article.getYear()).collect(Collectors.toList()));
        currentYears.addAll(productions.getBooks().stream().map(book -> book.getYear()).collect(Collectors.toList()));
        currentYears = currentYears.stream().distinct().sorted().collect(Collectors.toList());

        currentYears.forEach(year -> {
            int totalArticles =  productions.getArticles().stream().filter(article -> Objects.equals(article.getYear(), year)).collect(Collectors.toList()).size();
            int totalBooks = productions.getBooks().stream().filter(book -> Objects.equals(book.getYear(), year)).collect(Collectors.toList()).size();
            ColumnData data = new ColumnData(year, totalArticles, totalBooks);
            datas.add(new ColumnData(year, totalArticles, totalBooks));
        });

        return datas;
    }

    @Override
    public ColumnData getByYear(Integer year) {
        CollectionProduction productions = productionService.getAll();

        int totalArticles =  productions.getArticles().stream().filter(article -> Integer.parseInt(article.getYear()) == year).collect(Collectors.toList()).size();
        int totalBooks = productions.getBooks().stream().filter(book -> Integer.parseInt(book.getYear()) == year).collect(Collectors.toList()).size();
        ColumnData data = new ColumnData(year.toString(), totalArticles, totalBooks);

        return data;
    }

    @Override
    public List<ColumnData> getByYearRange(Integer startYear, Integer endYear) {
        List<ColumnData> datas = new ArrayList<>();
        List<String> currentYears = new ArrayList<>();

        CollectionProduction productions = productionService.getAll();

        currentYears.addAll(productions.getArticles().stream().map(article -> article.getYear()).collect(Collectors.toList()));
        currentYears.addAll(productions.getBooks().stream().map(book -> book.getYear()).collect(Collectors.toList()));
        currentYears = currentYears.stream().distinct().sorted().filter(year -> {
            return Integer.parseInt(year) >= startYear && Integer.parseInt(year) <= endYear;
        }).collect(Collectors.toList());

        currentYears.forEach(year -> {
            int totalArticles =  productions.getArticles().stream().filter(article -> Objects.equals(article.getYear(), year)).collect(Collectors.toList()).size();
            int totalBooks = productions.getBooks().stream().filter(book -> Objects.equals(book.getYear(), year)).collect(Collectors.toList()).size();

            datas.add(new ColumnData(year, totalArticles, totalBooks));
        });


        return datas;
    }

    @Override
    public CircleData getAllCircleData() {
        CollectionProduction productions = productionService.getAll();
        int totalArticles =  productions.getArticles().size();
        int totalBooks = productions.getBooks().size();

        CircleData data = new CircleData(totalArticles, totalBooks);
        return data;
    }
}


