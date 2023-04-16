package br.edu.femass.latteback.services;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.repositories.InstituteRepository;
import br.edu.femass.latteback.services.interfaces.InstituteServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.UUID;

@Service
public class InstituteService implements InstituteServiceInterface {

    private final InstituteRepository instituteRepository;

    public InstituteService(InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }

    @Override
    @Transactional
    public Institute save(Institute instituteDto) {
        if (instituteDto == null) {
            throw new IllegalArgumentException("Objeto instituto nulo");
        }

        var institute = new Institute();
        institute.setName(instituteDto.getName());
        institute.setAcronym(instituteDto.getAcronym());
        BeanUtils.copyProperties(instituteDto, institute);

        return instituteRepository.save(institute);
    }

    @Override
    public List<Institute> getAll() {
        return instituteRepository.findAll();
    }

    @Override
    public Institute getById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID procurado não pode ser nulo.");
        }

        var institute = instituteRepository.findById(id);

        if (!institute.isPresent()) {
            throw new IllegalArgumentException("Não há instituto cadastrado com esse id");
        }

        return institute.get();
    }

    @Override
    public void delete(UUID id) {

        var existInstitute = instituteRepository.existsById(id);

        if (!existInstitute) {
            throw new IllegalArgumentException("Não há instituto cadastrado com esse id");
        }

        instituteRepository.deleteById(id);
    }

    @Override
    public Institute update(UUID id, Institute institute) {
        if (institute == null) throw new IllegalArgumentException("Objeto instituto nulo");

        var foundInstitute = instituteRepository.findById(id);

        if (foundInstitute.isEmpty()) throw new NotFoundException("Instituto não encontrado");

        foundInstitute.get().setName(institute.getName());
        foundInstitute.get().setAcronym(institute.getAcronym());

        return instituteRepository.save(foundInstitute.get());
    }

    public Page<Institute> AdvancedSearch(String name, String acronym, Pageable pageable) {
        return instituteRepository.AdvancedSearch(name, acronym, pageable);
    }

}