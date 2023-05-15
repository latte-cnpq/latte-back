package br.edu.femass.latteback.controllers;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.PageProduction;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.services.ProductionService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/production")
@Tag(name = "Production", description = "This API provides endpoints for managing information about productions")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(schema = @Schema(implementation = CollectionProduction.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
})

public class ProductionController {
    
    private final ProductionService productionService;
    
    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

   @GetMapping("")
   @Operation(summary = "Find all productions")
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Successful operation",
                   content = {@Content(array = @ArraySchema(schema = @Schema(implementation = CollectionProduction.class)))
                   })
   })
   public ResponseEntity<Object> getAll() {
        try {
            CollectionProduction productions = productionService.getAll();
            return ResponseEntity.ok(productions);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    @Operation(summary = "Find production by ID")
    public ResponseEntity<Object> getById(
            @Parameter(description = "The ID that needs to be fetched.", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @RequestParam(name = "type", required = true) final ProductionType type) {
        try {
            var result = productionService.getById(id, type);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("getByResearcher/{id}")
    @Operation(summary = "Find productions by researcher ID")
    public ResponseEntity<Object> getByResearcher(
            @Parameter(description = "The ID that needs to be fetched.", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id) {
        try {
            var result = productionService.getAllByResearcher(id);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

   @GetMapping("/advancedsearch")
    @Operation(summary = "Find all productions using query search and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PageProduction.class))}
            ),
    })
    @Parameters({
        @Parameter(name = "title", description = "Title of the production", in = ParameterIn.QUERY),
        @Parameter(name = "startYear", description = "Start year of period", in = ParameterIn.QUERY, example = "2018"),
        @Parameter(name = "endYear", description = "End year of period", in = ParameterIn.QUERY, example = "2020"),
        @Parameter(name = "researcherName", description = "Name of researcher to search", in = ParameterIn.QUERY),
        @Parameter(name = "instituteName", description = "Name of institute to search", in = ParameterIn.QUERY),
        @Parameter(name = "type", description = "Production type to searcher", in = ParameterIn.QUERY),
        @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY, example = "0"),
        @Parameter(name = "perPage", description = "Number of items per page", in = ParameterIn.QUERY, example = "10"),
        @Parameter(name = "ordination", description = "Property used for sorting the results", in = ParameterIn.QUERY, example = "id"),
        @Parameter(name = "direction", description = "Sort direction (ASC or DESC)", in = ParameterIn.QUERY, example = "ASC")
    })
    public ResponseEntity<Object> findByAdvancedSearch(
        @RequestParam(name = "title", required = false) final String name,
        @RequestParam(name = "startYear", required = false) final Integer startYear,
        @RequestParam(name = "endYear", required = false) final Integer endYear,
        @RequestParam(name = "researcherName", required = false) final String researcherName,
        @RequestParam(name = "instituteName", required = false) final String instituteName,
        @RequestParam(name = "type", required = false, defaultValue = "ALL") final ProductionType type,
        @RequestParam(defaultValue = "0") final int page,
        @RequestParam(name = "perPage", defaultValue = "10") final int perPage,
        @RequestParam(defaultValue = "id") final String ordination,
        @RequestParam(defaultValue = "ASC") final Sort.Direction direction) {
            try {
                Integer size = 0;

                    switch (type) {
                        case BOOK:
                        case ARTICLE:
                            size = perPage;
                            break;
                        default:
                            size = (int) Math.ceil(perPage/2);
                            break;
                    }

                final Pageable pageable = PageRequest.of(page, size, Sort.by(direction, ordination));
                PageProduction productions = null;

                LocalDate startDate = startYear != null ? LocalDate.of(startYear, 01, 01) : null;
                LocalDate endDate = endYear != null ? LocalDate.of(endYear, 01, 01) : null;

                productions = productionService.AdvanceSearcher(name, startDate, endDate, researcherName, instituteName, type, pageable);
                return ResponseEntity.status(HttpStatus.OK).body(productions);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
}
