package br.edu.femass.latteback.services.interfaces;

import br.edu.femass.latteback.models.Institute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface InstituteServiceInterface {
    Institute save(Institute institute);

    List<Institute> getAll();

    Institute getById(UUID id);

    void delete(UUID id);

    Institute update(UUID id, Institute institute);

    Page<Institute> AdvancedSearch(String name, String acronym, Pageable pageable);
}
