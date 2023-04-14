package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.ResearcherCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResearcherCacheRepository extends JpaRepository<ResearcherCache, UUID> {
    List<ResearcherCache> findByNameContainsIgnoreCase(String name);

    Optional<ResearcherCache> findFirstByResearcherIdNumber(String ResearcherCacheidNumber);
}
