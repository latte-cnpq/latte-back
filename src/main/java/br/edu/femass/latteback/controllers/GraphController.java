package br.edu.femass.latteback.controllers;


import br.edu.femass.latteback.models.graph.Collaboration;
import br.edu.femass.latteback.models.graph.dto.GraphDataDTO;
import br.edu.femass.latteback.services.GraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/graph")
@Tag(name = "Graph", description = "This API provides endpoints for graph graph recovery")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(schema = @Schema(implementation = Collaboration.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)

})
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/collab/{name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
    })
    @Operation(summary = "Searches for a collaboration from a certain institute")
    public ResponseEntity<Object> searchCollaborationoByInstututeName(@PathVariable(value = "name") String instituteName) {

        try {

            var searchCollabByInstituteName = graphService.getCollaborationByInstituteName(instituteName);
            return ResponseEntity.status(HttpStatus.OK).body(searchCollabByInstituteName);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/collab")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(schema = @Schema(implementation = GraphDataDTO.class))}),
    })
    @Operation(summary = "Searches for a collaboration with filter")
    @Parameters({
            @Parameter(name = "researcherName", description = "Name of the researcher", in = ParameterIn.QUERY),
            @Parameter(name = "instituteName", description = "Name of the institute", in = ParameterIn.QUERY),
            @Parameter(name = "productionType", description = "Production type", in = ParameterIn.QUERY, example = "ARTICLE"),
            @Parameter(name = "nodeType", description = "Whether nodes are researchers or institutes", in = ParameterIn.QUERY)
    })
    public ResponseEntity<Object> filterCollaborations(
            @RequestParam(name = "researcherName", required = false) final String researcherName,
            @RequestParam(name = "instituteName", required = false) final String instituteName,
            @RequestParam(name = "productionType", required = false) final String productionType,
            @RequestParam(name = "nodeType", required = false) final String nodeType){

        try {
            var filterCollab = graphService.getGraphDataByFilter(instituteName, productionType, researcherName);

            return ResponseEntity.status(HttpStatus.OK).body(filterCollab);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
