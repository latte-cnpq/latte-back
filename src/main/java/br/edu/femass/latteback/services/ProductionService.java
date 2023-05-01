package br.edu.femass.latteback.services;
import br.edu.femass.latteback.models.Production;
import br.edu.femass.latteback.repositories.BookRepository;
import br.edu.femass.latteback.repositories.ArticleRepository;
import br.edu.femass.latteback.repositories.ProductionRepository;
import br.edu.femass.latteback.repositories.ResearcherCacheRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;
import br.edu.femass.latteback.services.interfaces.ProductionServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductionService implements ProductionServiceInterface {

    private final ProductionRepository productionRepository;
    private final ResearcherRepository researcherRepository;
    private final InstituteService instituteService;
    private final ResearcherCacheRepository researcherCacheRepository;
    private final ArticleRepository articleRepositoy;
    private final BookRepository bookRepository;

    public ProductionService(ProductionRepository productionRepository, ResearcherRepository researcherRepository, InstituteService instituteService, ResearcherCacheRepository researcherCacheRepository, ArticleRepository articleRepositoy, BookRepository bookRepository) {
        this.productionRepository = productionRepository;
        this.researcherRepository = researcherRepository;
        this.instituteService = instituteService;
        this.researcherCacheRepository = researcherCacheRepository;
        this.articleRepositoy = articleRepositoy;
        this.bookRepository = bookRepository;
    }

    public List<Production> getAll() {
        return productionRepository.findAll();
    }

    public Page<Production> AdvancedSearch(String productionType, String productionDetails, Pageable pageable){
        return productionRepository.AdvancedSearch(productionType, productionDetails, pageable);
    }
}
