package br.edu.femass.latteback.controllers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.edu.femass.latteback.models.Production;
import br.edu.femass.latteback.services.ProductionService;
//import br.edu.femass.latteback.services.InstituteService;
//import br.edu.femass.latteback.repositories.ProductionRepository;
//import br.edu.femass.latteback.services.ResearcherService;
//import br.edu.femass.latteback.models.Institute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/production")
@Tag(name = "Production", description = "This API provides endpoints for managing information about productions")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(schema = @Schema(implementation = Production.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
})

public class ProductionController {
    
    private final ProductionService productionService;
    //private final ResearcherService researcherService;
    //private final InstituteService instituteService;
    
    public ProductionController(ProductionService productionService/*, ResearcherService researcherService, InstituteService instituteService*/) {
        this.productionService = productionService;
        /*this.instituteService = instituteService;
        this.researcherService = researcherService;*/

    }

   @GetMapping("")
   @Operation(summary = "Find all productions")
   public ResponseEntity<Object> getAllProduction() {
       try {
           List<Production> productions = productionService.getAll();
           System.out.println(productions);
           return ResponseEntity.status(HttpStatus.OK).body(productions);
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
       }
   }

   @GetMapping("/advancedsearch")
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
        }
}
