package br.edu.femass.latteback.controllers;

import br.edu.femass.latteback.dto.ResearcherDto;
import br.edu.femass.latteback.services.ResearcherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/Researcher")
public class ResearcherController {
    
    private final ResearcherService ResearcherService;

    public ResearcherController(ResearcherService ResearcherService) {
        this.ResearcherService = ResearcherService;
    }

    //region Queries
    @GetMapping("/GetAll")
    public ResponseEntity<Object> getAllResearcher() {
        try {
            var Researchers = ResearcherService.getAll();
            return ResponseEntity.status(HttpStatus.OK).body(Researchers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable(value = "id") UUID id) {
        try {
            var Researcher = ResearcherService.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(Researcher);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /*@GetMapping("/ExecuteFilter")
    public ResponseEntity<List<Researcher>> executeFilter(
            @RequestParam(value = "textSearch") String textSearch,
            @RequestParam(value = "field") ResearcherField field
    ) {
        var Researchers = ResearcherService.filterResearcherByTextSearch(textSearch, field);
        return ResponseEntity.status(HttpStatus.OK).body(Researchers);
    }*/
    //TODO:Filtragem de pesquisador
    //endregion

    //region Command
    @PostMapping("/Create/{idNumber}")
    public ResponseEntity<Object> createResearcher(@PathVariable(value = "idNumber") String researcheridNumber) {
        try {
            var result = ResearcherService.save(researcheridNumber);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/Update")
    public ResponseEntity<Object> updateResearcher(@RequestBody ResearcherDto dto) {
        try {
            var result = ResearcherService.update(dto);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Object> deleteResearcher(@PathVariable(value = "id") UUID id) {
        try {
            ResearcherService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(e.getMessage());
        }
    }


    //endregion


}
