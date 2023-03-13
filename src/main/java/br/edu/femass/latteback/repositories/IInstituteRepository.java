package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IInstituteRepository extends JpaRepository<Institute, UUID> {
    List<Institute> findByNameContainsIgnoreCaseOrAcronymContainsIgnoreCase(String name, String acronym);
    List<Institute> findByNameContainsIgnoreCase(String name);
    List<Institute> findByAcronymContainsIgnoreCase(String acronym);


}
