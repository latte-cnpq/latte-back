package br.edu.femass.latteback.services;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
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
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.repositories.ResearcherRepository;

@ExtendWith(MockitoExtension.class)
public class ResearcherServiceTest {
    @Mock
    private ResearcherRepository researcherRepository;

    @InjectMocks
    private ResearcherService researcherService;
    private static final Researcher researcher = new Researcher();
    private static UUID id;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    
    @Test
    @DisplayName("Get all saved researchers")
    public void getAllTest() {
        Researcher researcher1 = new Researcher();
        researcher1.setName("Test");
        researcher1.setResearcheridNumber("");

        when(researcherService.getAll()).thenReturn(List.of(researcher, researcher1));

        List<Researcher> savedResearchers = new ArrayList<>();
        savedResearchers.add(researcher);
        savedResearchers.add(researcher1);

        assertEquals(savedResearchers, researcherService.getAll());
        assertThat(savedResearchers).isNotNull();
        assertThat(savedResearchers.size()).isEqualTo(2);
    }
    



}
