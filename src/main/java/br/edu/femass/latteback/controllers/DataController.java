package br.edu.femass.latteback.controllers;

import br.edu.femass.latteback.models.charts.CircleData;
import br.edu.femass.latteback.models.charts.ColumnData;
import br.edu.femass.latteback.models.charts.CounterData;
import br.edu.femass.latteback.services.DataService;
import br.edu.femass.latteback.utils.enums.ProductionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/data")
@Tag(name = "Data", description = "This API provides endpoints for data charts")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(schema = @Schema(implementation = ColumnData.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)

})
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    //region columns

    @GetMapping("columns")
    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ColumnData.class)))
                    })
    })
    public ResponseEntity<Object> getTotalProductionsColumnData() {
        try {
            List<ColumnData> result = dataService.getAllColumnData();
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("columns/productionsByYear")
    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ColumnData.class)))
                    })
    })
    @Parameters({
            @Parameter(name = "year", description = "The year that needs to be fetched.", required = true, example = "2018")
    })
    public ResponseEntity<Object> getProductionsByYear(
            @RequestParam(name = "year", required = true) final Integer year) {
        try {
            ColumnData result = dataService.getByYear(year);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("columns/getProductionsByYearRange")
    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ColumnData.class)))
                    })
    })
    @Parameters({
            @Parameter(name = "startYear", description = "The start year that needs to be fetched.", required = true, example = "2018"),
            @Parameter(name = "endYear", description = "The end year that needs to be fetched.", required = true, example = "2021"),
    })
    public ResponseEntity<Object> getProductionsByYearRange(
            @RequestParam(name = "startYear", required = true) final Integer startYear,
            @RequestParam(name = "endYear", required = true) final Integer endYear) {
        try {
            List<ColumnData> results = dataService.getByYearRange(startYear, endYear);
            return ResponseEntity.ok(results);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("columns/getProductionsByFilter")
    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ColumnData.class)))
                    })
    })
    @Parameters({
            @Parameter(name = "startYear", description = "Start year of period", in = ParameterIn.QUERY, example = "2018"),
            @Parameter(name = "endYear", description = "End year of period", in = ParameterIn.QUERY, example = "2021"),
            @Parameter(name = "researcherName", description = "Name of researcher to search", in = ParameterIn.QUERY),
            @Parameter(name = "instituteName", description = "Name of institute to search", in = ParameterIn.QUERY),
            @Parameter(name = "type", description = "Production type to searcher", in = ParameterIn.QUERY)
    })
    public ResponseEntity<Object> getProductionsByFilter(
            @RequestParam(name = "startYear", required = false) final Integer startYear,
            @RequestParam(name = "endYear", required = false) final Integer endYear,
            @RequestParam(name = "researcherName", required = false) final String researcherName,
            @RequestParam(name = "instituteName", required = false) final String instituteName,
            @RequestParam(name = "type", required = false, defaultValue = "ALL") final ProductionType type) {
        try {
            List<ColumnData> results = dataService.getColumnDataByFilter(startYear, endYear, researcherName, instituteName, type);
            return ResponseEntity.ok(results);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //endregion

    //region circles

    @GetMapping("circles")
    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = CircleData.class)))
                    })
    })
    public ResponseEntity<Object> getTotalProductionsCircleData() {
        try {
            CircleData result = dataService.getAllCircleData();
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //endregion

    //region counter

    @GetMapping("counters")
    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = CounterData.class)))
                    })
    })
    public ResponseEntity<Object> getCounters() {
        try {
            CounterData result = dataService.getCounterData();
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    //endregion
}
