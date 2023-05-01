package br.edu.femass.latteback.services;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import br.edu.femass.latteback.models.Production;
import br.edu.femass.latteback.repositories.ProductionRepository;

@ExtendWith(MockitoExtension.class)
public class ProdutionServiceTest {
    @Mock
    private ProductionRepository productionRepository;

    @InjectMocks
    private ProductionService productionService;
    private static final Production production = new Production();
    private static UUID id;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Get all productions") 
    public void getAllTest() {
        Production production1 = new Production();
        production1.setProductionType("Test");;
        production1.setProductionDetails("Test");

        when(productionService.getAll()).thenReturn(List.of(production, production1));

        List<Production> savedProduction = new ArrayList<>();
        savedProduction.add(production);
        savedProduction.add(production1);

        assertEquals(savedProduction, productionService.getAll());
        assertThat(savedProduction).isNotNull();
        assertThat(savedProduction.size()).isEqualTo(2);
    }   
}