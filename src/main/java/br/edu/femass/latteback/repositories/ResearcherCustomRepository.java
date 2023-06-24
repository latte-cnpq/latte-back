package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Researcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResearcherCustomRepository {
    Page<Researcher> AdvancedSearch(String name, String instituteAcronym, Pageable pageable);
}
