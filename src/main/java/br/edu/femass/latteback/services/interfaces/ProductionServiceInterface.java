package br.edu.femass.latteback.services.interfaces;
import java.util.List;
import br.edu.femass.latteback.models.Production;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductionServiceInterface {
   List<Production> getAll();
   
   //Production save();
   //Production getById(UUID id);
   //void delete(UUID id);
   //Production update(UUID id, Production production);

   /*Page<Production> AdvancedSearch(String name, String productionType, String productionDetails,
   org.springdoc.core.converters.models.Pageable pageable);*/

   Page<Production> AdvancedSearch(String name, String productionType, String productionDetails, Pageable pageable);
}
