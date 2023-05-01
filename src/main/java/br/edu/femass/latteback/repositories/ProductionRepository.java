package br.edu.femass.latteback.repositories;
import br.edu.femass.latteback.models.Production;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductionRepository extends JpaRepository<Production, UUID> {
    Page<Production> AdvancedSearch(String productionType, String productionDetails, org.springframework.data.domain.Pageable pageable);
}
