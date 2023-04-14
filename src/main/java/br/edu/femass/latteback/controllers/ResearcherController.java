package br.edu.femass.latteback.controllers;

import br.edu.femass.latteback.dto.ResearcherFormDTO;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.models.ResearcherCache;
import br.edu.femass.latteback.repositories.ResearcherCacheRepository;
import br.edu.femass.latteback.services.InstituteService;
import br.edu.femass.latteback.services.ResearcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/researcher")
@Tag(name = "Researcher", description = "This API provides endpoints for managing information about researchers")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(schema = @Schema(implementation = Researcher.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
})

public class ResearcherController {
    private final ResearcherService researcherService;
    private final InstituteService instituteService;
    private final ResearcherCacheRepository ResearcherCacheService;

    public ResearcherController(ResearcherService ResearcherService, InstituteService InstituteService, ResearcherCacheRepository ResearcherCacheService) {
        this.researcherService = ResearcherService;
        this.instituteService = InstituteService;
        this.ResearcherCacheService = ResearcherCacheService;
    }

    //region Queries
    @GetMapping("")
    @Operation(summary = "Find all researchers")
    public ResponseEntity<Object> getAllResearcher() {
        try {
            List<Researcher> researchers = researcherService.getAll();
            System.out.println(researchers);
            return ResponseEntity.status(HttpStatus.OK).body(researchers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find researcher by ID")
    public ResponseEntity<Object> getById(
            @Parameter(description = "The ID that needs to be fetched.", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable(value = "id") UUID id) {
        try {
            var Researcher = researcherService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(Researcher);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/cache/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(schema = @Schema(implementation = ResearcherCache.class))}),
    })
    @Operation(summary = "Searches for a researcher file on cache")
    public ResponseEntity<Object> searchResearcher(@PathVariable(value = "id") String researcherIdNumber) {
        try {
            var researcherCache = ResearcherCacheService.findFirstByResearcherIdNumber(researcherIdNumber);

            if (researcherCache.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(researcherCache);
            }

            var searchResearcherCache = researcherService.searchFiles(researcherIdNumber);

            return ResponseEntity.status(HttpStatus.OK).body(searchResearcherCache);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/advancedsearch")
    @Operation(summary = "Find all researchers using query search and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))}
            ),
    })
    @Parameters({
            @Parameter(name = "name", description = "Name of the institute", in = ParameterIn.QUERY),
            @Parameter(name = "institute_acronym", description = "Acronym of the institute", in = ParameterIn.QUERY),
            @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "perPage", description = "Number of items per page", in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "ordination", description = "Property used for sorting the results", in = ParameterIn.QUERY, example = "id"),
            @Parameter(name = "direction", description = "Sort direction (ASC or DESC)", in = ParameterIn.QUERY, example = "ASC")
    })
    public ResponseEntity<Object> findByAdvancedSearch(
            @RequestParam(name = "name", required = false) final String name,
            @RequestParam(name = "institute_acronym", required = false) final String acronym,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(name = "perPage", defaultValue = "10") final int perPage,
            @RequestParam(defaultValue = "id") final String ordination,
            @RequestParam(defaultValue = "ASC") final Sort.Direction direction) {

        try {
            final Pageable pageable = PageRequest.of(page, perPage, Sort.by(direction, ordination));
            Page<Researcher> researchers = null;

            researchers = researcherService.AdvancedSearch(name, acronym, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(researchers);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("")
    @Operation(summary = "Create a new researcher")
    public ResponseEntity<Object> createResearcher(@RequestBody ResearcherFormDTO formData) {
        try {
            var result = researcherService.save(formData.getResearcherIdNumber(), formData.getInstituteId());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates a researcher")
    public ResponseEntity<Object> updateResearcher(
            @Parameter(description = "The ID of the researcher to update.", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable(value = "id") UUID id,
            @RequestBody Researcher researcher) {
        try {
            var result = researcherService.update(id, researcher);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a institute")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content)})
    public ResponseEntity<Object> deleteResearcher(@PathVariable(value = "id") UUID id) {
        try {
            researcherService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }


}
