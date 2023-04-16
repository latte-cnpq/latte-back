package br.edu.femass.latteback.controllers;

import br.edu.femass.latteback.dto.InstituteDto;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.services.InstituteService;
import br.edu.femass.latteback.utils.enums.InstituteField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/Institute")
public class InstituteController {
    private final InstituteService instituteService;

    public InstituteController(InstituteService instituteService) {
        this.instituteService = instituteService;
    }

    //region Queries
    @GetMapping("/GetAll")
    public ResponseEntity<Object> getAllInstitute() {
        try {
            var institutes = instituteService.getAll();
            return ResponseEntity.status(HttpStatus.OK).body(institutes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable(value = "id") UUID id) {
        try {
            var institute = instituteService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(institute);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/ExecuteFilter")
    public ResponseEntity<List<Institute>> executeFilter(
            @RequestParam(value = "textSearch") String textSearch,
            @RequestParam(value = "field") InstituteField field
    ) {
        var institutes = instituteService.filterInstituteByTextSearch(textSearch, field);
        return ResponseEntity.status(HttpStatus.OK).body(institutes);
    }

    //endregion

    //region Command
    @PostMapping("/Create")
    public ResponseEntity<Object> createInstitute(@RequestBody InstituteDto dto) {
        try {
            var result = instituteService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }

    @PutMapping("/Update")
    public ResponseEntity<Object> updateInstitute(@RequestBody InstituteDto dto) {
        try {
            var result = instituteService.update(dto);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Object> deleteInstitute(@PathVariable(value = "id") UUID id) {
        try {
            instituteService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }


    //endregion


}
