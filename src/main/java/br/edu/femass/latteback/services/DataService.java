package br.edu.femass.latteback.services;

import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.models.charts.CircleData;
import br.edu.femass.latteback.models.charts.ColumnData;
import br.edu.femass.latteback.services.interfaces.DataServiceInterface;
import br.edu.femass.latteback.utils.enums.ProductionType;
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
    public List<ColumnData> getColumnDataByFilter(Integer startYear, Integer endYear, String researcherName, String instituteName, ProductionType type) {
        List<ColumnData> datas = new ArrayList<>();
        List<String> currentYears = new ArrayList<>();

        CollectionProduction productions = productionService.getAll();

        currentYears.addAll(productions.getArticles().stream().map(article -> article.getYear()).collect(Collectors.toList()));
        currentYears.addAll(productions.getBooks().stream().map(book -> book.getYear()).collect(Collectors.toList()));

        if(startYear != null && endYear == null) {
            currentYears = currentYears.stream().distinct().sorted().filter(year -> {
                return Integer.parseInt(year) >= startYear;
            }).collect(Collectors.toList());
        } else if(startYear == null && endYear != null) {
            currentYears = currentYears.stream().distinct().sorted().filter(year -> {
                return  Integer.parseInt(year) <= endYear;
            }).collect(Collectors.toList());
        } else if(startYear != null && endYear != null) {
            currentYears = currentYears.stream().distinct().sorted().filter(year -> {
                return Integer.parseInt(year) >= startYear && Integer.parseInt(year) <= endYear;
            }).collect(Collectors.toList());
        } else {
            currentYears = currentYears.stream().distinct().sorted().collect(Collectors.toList());
        }

        currentYears.forEach(year -> {
            List<Article> filteredArticles = productions.getArticles().stream().filter(article -> Objects.equals(article.getYear(), year)).collect(Collectors.toList());
            List<Book> filteredBooks = productions.getBooks().stream().filter(book -> Objects.equals(book.getYear(), year)).collect(Collectors.toList());

            if(researcherName != null && !researcherName.isEmpty()) {
                filteredArticles = filteredArticles.stream().filter(article -> article.getResearcher().getName().contains(researcherName)).collect(Collectors.toList());
                filteredBooks = filteredBooks.stream().filter(book ->  book.getResearcher().getName().contains(researcherName)).collect(Collectors.toList());
            }

            if(instituteName != null && !instituteName.isEmpty()) {
                filteredArticles = filteredArticles.stream().filter(article -> article.getResearcher().getInstitute().getName().contains(instituteName)).collect(Collectors.toList());
                filteredBooks = filteredBooks.stream().filter(book ->  book.getResearcher().getInstitute().getName().contains(instituteName)).collect(Collectors.toList());
            }

            int totalArticles = 0;
            int totalBooks = 0;

            switch (type) {
                case ARTICLE:
                    totalArticles =  filteredArticles.size();
                    break;
                case BOOK:
                    totalBooks = filteredBooks.size();
                    break;
                default:
                    totalArticles =  filteredArticles.size();
                    totalBooks = filteredBooks.size();
                    break;
            }

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


