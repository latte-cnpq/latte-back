package br.edu.femass.latteback.services.interfaces;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.ProductionInterface;
import br.edu.femass.latteback.utils.enums.ProductionType;
import java.time.LocalDate;
import java.util.UUID;

public interface ProductionServiceInterface {
    CollectionProduction getAll();

    CollectionProduction getAllByResearcher(UUID researcherId);

    CollectionProduction getAllByInstitute(UUID instituteId);

    ProductionInterface getById(UUID productionId, ProductionType type);

    CollectionProduction AdvancedSearch(String title, UUID researcherId, UUID instituteId, LocalDate startDate, LocalDate endDate, ProductionType type);
}
