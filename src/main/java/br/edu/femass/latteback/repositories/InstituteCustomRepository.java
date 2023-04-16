package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Institute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstituteCustomRepository {
    Page<Institute> AdvancedSearch(String name, String acronym, Pageable pageable);
}
