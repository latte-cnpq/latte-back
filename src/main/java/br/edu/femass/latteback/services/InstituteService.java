package br.edu.femass.latteback.services;

import br.edu.femass.latteback.dto.InstituteDto;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.repositories.IInstituteRepository;
import br.edu.femass.latteback.services.interfaces.IInstituteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InstituteService implements IInstituteService {

    private final IInstituteRepository _instituteRepository;

    public InstituteService(IInstituteRepository instituteRepository) {

        this._instituteRepository = instituteRepository;
    }

    @Override
    @Transactional
    public Institute save(InstituteDto instituteDto) {
        if(instituteDto == null) {
            throw new IllegalArgumentException("Objeto instituto nulo");
        }

        var institute = new Institute();
        institute.setName(instituteDto.getName());
        institute.setAcronym(instituteDto.getAcronym());
        BeanUtils.copyProperties(instituteDto, institute);

        return _instituteRepository.save(institute);
    }

    @Override
    public List<Institute> getAll() {
        return _instituteRepository.findAll();
    }

    @Override
    public Institute getById(UUID id) {
        var institute = _instituteRepository.findById(id);

        if(!institute.isPresent()) {
            throw new IllegalArgumentException("Não há instituto cadastrado");
        }

        return  institute.get();
    }

    @Override
    public void delete(UUID id) {

        var existInstitute = _instituteRepository.existsById(id);

        if(!existInstitute) {
            throw new IllegalArgumentException("Não há instituto cadastrado com esse id");
        }

        _instituteRepository.deleteById(id);
    }

    @Override
    public Institute update(InstituteDto instituteDto) {
        if(instituteDto == null) {
            throw new IllegalArgumentException("Objeto instituto nulo");

        }

        var foundInstitute = _instituteRepository.findById(instituteDto.getId());

        if(!foundInstitute.isPresent()) {
            throw new IllegalArgumentException("Instituto não encontrado");
        }

        foundInstitute.get().setName(instituteDto.getName());
        foundInstitute.get().setAcronym(instituteDto.getAcronym());

        return _instituteRepository.save(foundInstitute.get());
    }

    @Override
    public List<Institute> filterInstituteByTextSearch(String textSearch, int field) {
        if(textSearch.isBlank() || textSearch.isEmpty()) {
           return getAll();
        }

        return switch (field) {
            case 1 -> _instituteRepository.findByNameContainsIgnoreCase(textSearch);
            case 2 -> _instituteRepository.findByAcronymContainsIgnoreCase(textSearch);
            default -> _instituteRepository.findByNameContainsIgnoreCaseOrAcronymContainsIgnoreCase(textSearch, textSearch);
        };
    }
}
