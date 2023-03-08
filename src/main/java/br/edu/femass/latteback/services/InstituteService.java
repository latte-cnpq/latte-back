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

    private final IInstituteRepository instituteRepository;

    public InstituteService(IInstituteRepository instituteRepository) {

        this.instituteRepository = instituteRepository;
    }

    @Override
    @Transactional
    public Institute save(InstituteDto instituteDto) {
        if(instituteDto == null) {
            throw new IllegalArgumentException("Objeto instituto nulo");
        }

        var institute = new Institute();
        BeanUtils.copyProperties(instituteDto, institute);

        return instituteRepository.save(institute);
    }

    @Override
    public List<Institute> getAll() {
        return instituteRepository.findAll();
    }

    @Override
    public Institute getById(UUID id) {
        var institute = instituteRepository.findById(id);

        if(!institute.isPresent()) {
            throw new IllegalArgumentException("Não há instituto cadastrado");
        }

        return  institute.get();
    }

    @Override
    public void delete(UUID id) {
        var institute = instituteRepository.findById(id);

        if(!institute.isPresent()) {
            throw new IllegalArgumentException("Não há instituto cadastrado");
        }
        instituteRepository.delete(institute.get());
    }

    @Override
    public Institute update(InstituteDto instituteDto) {
        if(instituteDto == null) {
            throw new IllegalArgumentException("Objeto instituto nulo");
        }

        var institute = new Institute();
        BeanUtils.copyProperties(instituteDto, institute);
        institute.setId(instituteDto.getId());

        return instituteRepository.save(institute);
    }
}
