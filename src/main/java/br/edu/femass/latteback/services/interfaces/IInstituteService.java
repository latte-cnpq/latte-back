package br.edu.femass.latteback.services.interfaces;

import br.edu.femass.latteback.dto.InstituteDto;
import br.edu.femass.latteback.models.Institute;

import java.util.List;
import java.util.UUID;
public interface IInstituteService {
    Institute save(InstituteDto instituteDto);
    List<Institute> getAll();
    Institute getById(UUID id);
    void delete(UUID id);
    Institute update(InstituteDto instituteDto);
    List<Institute> filterInstituteByTextSearch(String textSearch, int field);
}
