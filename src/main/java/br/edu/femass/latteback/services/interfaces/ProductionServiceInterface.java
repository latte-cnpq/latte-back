package br.edu.femass.latteback.services.interfaces;
import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.PageProduction;
import br.edu.femass.latteback.dto.ProductionInterface;
import br.edu.femass.latteback.utils.enums.ProductionType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ProductionServiceInterface {
    CollectionProduction getAll();
    CollectionProduction getAllByResearcher(UUID researcherId);

    ProductionInterface getById(UUID productionId, ProductionType type);

    PageProduction AdvanceSearcher(String title, LocalDate startDate, LocalDate endDate, String researcherName, String instituteName, ProductionType type, Pageable pageable);
}
