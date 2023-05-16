package br.edu.femass.latteback.services.interfaces;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ResearcherServiceInterface {
    Researcher save(String researcherIdNumber, UUID instituteId);

    List<Researcher> getAll();

    Researcher getById(UUID id);

    void delete(UUID id);

    Researcher update(UUID id, Researcher researcher);

    Long getResearcherTotalCount();

    Page<Researcher> AdvancedSearch(String name, String instituteAcronym, Pageable pageable);

}
