package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, UUID>, InstituteCustomRepository {
    Institute findInstituteByAcronymIs(String acronym);
}
