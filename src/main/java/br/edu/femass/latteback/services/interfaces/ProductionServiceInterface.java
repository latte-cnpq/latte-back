package br.edu.femass.latteback.services.interfaces;

import br.edu.femass.latteback.dto.CollectionProduction;
import br.edu.femass.latteback.dto.ProductionInterface;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.utils.enums.ProductionType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProductionServiceInterface {
    CollectionProduction getAll();
    CollectionProduction getAllByResearcher(UUID researcherId);

    ProductionInterface getById(UUID productionId, ProductionType type);

    CollectionProduction AdvanceSearcher(String title, UUID researcherId, UUID instituteId, LocalDate startDate, LocalDate endDate, ProductionType type);
}
