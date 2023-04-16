package br.edu.femass.latteback.controllers;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.services.InstituteService;
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
@RequestMapping("/institute")
@Tag(name = "Institute", description = "This API provides endpoints for managing information about institutes")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", content = {@Content(schema = @Schema(implementation = Institute.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)

})

public class InstituteController {

    private final InstituteService instituteService;

    public InstituteController(InstituteService instituteService) {
        this.instituteService = instituteService;
    }

    @GetMapping("")
    @Operation(summary = "Find all institutes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Institute.class)))
                    })
    })
    public ResponseEntity<Object> getAll() {
        try {
            List<Institute> institutes = instituteService.getAll();
            return ResponseEntity.status(HttpStatus.OK).body(institutes);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/advancedsearch")
    @Operation(summary = "Find all institutes using query search and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))}
            ),
    })
    @Parameters({
            @Parameter(name = "name", description = "Name of the institute", in = ParameterIn.QUERY),
            @Parameter(name = "acronym", description = "Acronym of the institute", in = ParameterIn.QUERY),
            @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "perPage", description = "Number of items per page", in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "ordination", description = "Property used for sorting the results", in = ParameterIn.QUERY, example = "id"),
            @Parameter(name = "direction", description = "Sort direction (ASC or DESC)", in = ParameterIn.QUERY, example = "ASC")
    })
    public ResponseEntity<Object> findByAdvancedSearch(
            @RequestParam(name = "name", required = false) final String name,
            @RequestParam(name = "acronym", required = false) final String acronym,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(name = "perPage", defaultValue = "10") final int perPage,
            @RequestParam(defaultValue = "id") final String ordination,
            @RequestParam(defaultValue = "ASC") final Sort.Direction direction) {

        try {
            final Pageable pageable = PageRequest.of(page, perPage, Sort.by(direction, ordination));
            Page<Institute> institutes = null;

            institutes = instituteService.AdvancedSearch(name, acronym, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(institutes);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find institute by ID")
    public ResponseEntity<Object> getById(
            @Parameter(description = "The ID that needs to be fetched.", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id) {
        try {
            var institute = instituteService.getById(id);
            return ResponseEntity.ok(institute);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("")
    @Operation(summary = "Create a new institute")
    public ResponseEntity<Object> createInstitute(@RequestBody Institute institute) {

        System.out.println(institute.toString());
        try {
            var result = instituteService.save(institute);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates a institute")
    public ResponseEntity<Object> updateInstitute(
            @Parameter(description = "The ID of the institute to update.", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable(value = "id") UUID id,
            @RequestBody Institute institute) {
        try {
            var result = instituteService.update(id, institute);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a institute")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content)})
    public ResponseEntity<Object> deleteInstitute(
            @Parameter(description = "The ID that needs to be updated.", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable(value = "id") UUID id) {
        try {
            instituteService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }

}
