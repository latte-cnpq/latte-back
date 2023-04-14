package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Researcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResearcherCustomRepository {
    Page<Researcher> AdvancedSearch(String name, String institueAcronym, Pageable pageable);
}
