package br.edu.femass.latteback.controllers;

import br.edu.femass.latteback.dto.ResearcherDto;
import br.edu.femass.latteback.dto.ResearcherWithInstituteDTO;
import br.edu.femass.latteback.dto.SearchResearchDto;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.dto.ResearcherFormDto;
import br.edu.femass.latteback.repositories.ResearcherCacheRepository;
import br.edu.femass.latteback.services.InstituteService;
import br.edu.femass.latteback.services.ResearcherService;
import br.edu.femass.latteback.utils.enums.ResearcherField;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/Researcher")
public class ResearcherController {
    private final ResearcherService ResearcherService;
    private final InstituteService InstituteService;
    private final ResearcherCacheRepository ResearcherCacheService;

    public ResearcherController(ResearcherService ResearcherService, InstituteService InstituteService, ResearcherCacheRepository ResearcherCacheService) {
        this.ResearcherService = ResearcherService;
        this.InstituteService = InstituteService;
        this.ResearcherCacheService = ResearcherCacheService;
    }

    //region Queries
    @GetMapping("/GetAll")
    public ResponseEntity<Object> getAllResearcher() {
        try {
            List<ResearcherWithInstituteDTO> researchersWithInstitutes = ResearcherService.getAll()
                    .stream()
                    .map(researcher -> {
                        ResearcherWithInstituteDTO dto = new ResearcherWithInstituteDTO();
                        dto.setId(researcher.getId());
                        dto.setName(researcher.getName());
                        dto.setEmail(researcher.getEmail());
                        dto.setResearcheridNumber(researcher.getResearcheridNumber());
                        dto.setResume(researcher.getResume());

                        Institute institute = InstituteService.getById(researcher.getInstituteID());
                        dto.setInstituteId(institute.getId());
                        dto.setInstituteName(institute.getName());
                        dto.setInstituteAcronym(institute.getAcronym());

                        return dto;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(researchersWithInstitutes);
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

    @GetMapping("/SearchResearcher")
    public ResponseEntity<Object> searchResearcher(@RequestBody SearchResearchDto dto) {
        try {
//            var Researcher = ResearcherService.searchFiles(dto.getResearcherIdNumber());
            var researcherCache = ResearcherCacheService.findFirstByResearcheridNumber(dto.getResearcherIdNumber());

            if(researcherCache.isPresent()){
                return ResponseEntity.status(HttpStatus.OK).body(researcherCache);
            }

            var searchResearcherCache = ResearcherService.searchFiles(dto.getResearcherIdNumber());

            return ResponseEntity.status(HttpStatus.OK).body(searchResearcherCache);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/ExecuteFilter")
    public ResponseEntity<List<ResearcherWithInstituteDTO>> executeFilter(
            @RequestParam(value = "textSearch") String textSearch,
            @RequestParam(value = "field") ResearcherField field) {
        List<ResearcherWithInstituteDTO> researchersWithInstitutes = ResearcherService.filterResearcherByTextSearch(textSearch, field)
                .stream()
                .map(researcher -> {
                    ResearcherWithInstituteDTO dto = new ResearcherWithInstituteDTO();
                    dto.setId(researcher.getId());
                    dto.setName(researcher.getName());
                    dto.setEmail(researcher.getEmail());
                    dto.setResearcheridNumber(researcher.getResearcheridNumber());
                    dto.setResume(researcher.getResume());

                    Institute institute = InstituteService.getById(researcher.getInstituteID());
                    dto.setInstituteId(institute.getId());
                    dto.setInstituteName(institute.getName());
                    dto.setInstituteAcronym(institute.getAcronym());

                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(researchersWithInstitutes);
    }
    
    //endregion

    //region Command
    @PostMapping("/Create")
    public ResponseEntity<Object> createResearcher(@RequestBody ResearcherFormDto formData) {

        try {
            var result = ResearcherService.save(formData.getResearcherIdNumber(), formData.getInstituteId());
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
