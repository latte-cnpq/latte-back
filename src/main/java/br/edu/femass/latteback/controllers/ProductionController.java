package br.edu.femass.latteback.controllers;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Production;
import br.edu.femass.latteback.services.InstituteService;
import br.edu.femass.latteback.repositories.ProductionRepository;
import br.edu.femass.latteback.services.ProductionService;
import br.edu.femass.latteback.services.ResearcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
    private final ResearcherService researcherService;
    private final InstituteService instituteService;
    
    public ProductionController(ProductionService productionService, ResearcherService researcherService, InstituteService instituteService) {
        this.productionService = productionService;
        this.instituteService = instituteService;
        this.researcherService = researcherService;

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

}
