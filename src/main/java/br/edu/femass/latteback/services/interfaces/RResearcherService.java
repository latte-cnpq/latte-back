package br.edu.femass.latteback.services.interfaces;

import br.edu.femass.latteback.dto.ResearcherDto;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.models.ResearcherCache;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface RResearcherService {
    Researcher save(String researcheridNumber);
    List<Researcher> getAll();
    Researcher getById(UUID id);
    void delete(UUID id);
    Researcher update(ResearcherDto researcherDto);
    Optional<ResearcherCache> findResearcherOnCache(String researcheridNumber);

}
