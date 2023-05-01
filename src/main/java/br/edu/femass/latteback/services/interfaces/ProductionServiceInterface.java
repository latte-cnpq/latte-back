package br.edu.femass.latteback.services.interfaces;
import java.util.List;
import java.util.UUID;
import br.edu.femass.latteback.models.Production;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductionServiceInterface {
   //Production save();

   List<Production> getAll();

   //Production getById(UUID id);

   //void delete(UUID id);

   //Production update(UUID id, Production production);

   Page<Production> AdvancedSearch(String productionType, String productionDetails, Pageable pageable);
}
