package br.edu.femass.latteback.repositories;
import br.edu.femass.latteback.models.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InstituteRepository extends JpaRepository<Institute, UUID>, InstituteCustomRepository {
    Institute findInstituteByAcronymIs(String acronym);
}