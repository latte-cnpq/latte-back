package br.edu.femass.latteback.controllers;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.services.ProductionService;
import br.edu.femass.latteback.utils.enums.ProductionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
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

   /*@GetMapping("/advancedsearch")
    @Operation(summary = "Find all productions using query search and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))}
            ),
    })
    @Parameters({
        @Parameter(name = "name", description = "Name of the researcher", in = ParameterIn.QUERY),
        @Parameter(name = "production_type", description = "Type of production", in = ParameterIn.QUERY),
        @Parameter(name = "production_details", description = "Details of production", in = ParameterIn.QUERY),
        @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY, example = "0"),
        @Parameter(name = "perPage", description = "Number of items per page", in = ParameterIn.QUERY, example = "10"),
        @Parameter(name = "ordination", description = "Property used for sorting the results", in = ParameterIn.QUERY, example = "id"),
        @Parameter(name = "direction", description = "Sort direction (ASC or DESC)", in = ParameterIn.QUERY, example = "ASC")
    })
    public ResponseEntity<Object> findByAdvancedSearch(
        @RequestParam(name = "name", required = false) final String name,
        @RequestParam(name = "production_type", required = false) final String productionType,
        @RequestParam(name = "production_details", required = false) final String productionDetails,
        @RequestParam(defaultValue = "0") final int page,
            @RequestParam(name = "perPage", defaultValue = "10") final int perPage,
            @RequestParam(defaultValue = "id") final String ordination,
            @RequestParam(defaultValue = "ASC") final Sort.Direction direction) {
            try {
                final Pageable pageable = PageRequest.of(page, perPage, Sort.by(direction, ordination));
                Page<Production> productions = null;
                productions = productionService.AdvancedSearch(name, productionType, productionDetails, pageable);
                return ResponseEntity.status(HttpStatus.OK).body(productions);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }*/
}
