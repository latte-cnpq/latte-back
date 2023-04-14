package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResearcherRepository extends JpaRepository<Researcher, UUID>, ResearcherCustomRepository {
    List<Researcher> findByNameContainsIgnoreCase(String name);

    Optional<Researcher> findFirstByResearcheridNumberContainsIgnoreCase(String researcheridNumber);

    //List<Researcher> findByNameStartsWith(String name);
    List<Researcher> findByNameContainsIgnoreCaseOrResearcheridNumber(String name, String researcheridNumber);
}
