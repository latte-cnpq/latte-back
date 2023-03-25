package br.edu.femass.latteback.services;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import br.edu.femass.latteback.dto.ResearcherDto;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.repositories.ResearcherRepository;

@ExtendWith(MockitoExtension.class)
public class ResearcherServiceTest {
    @Mock
    private ResearcherRepository researcherRepository;

    @InjectMocks
    private ResearcherService researcherService;
    private static ResearcherDto researcherDto = new ResearcherDto();
    private static final Researcher researcher = new Researcher();
    private static UUID id;

    @BeforeEach
    public void setup() {
        researcherDto = new ResearcherDto();
        researcherDto.setName("Pesquisador1");
        researcherDto.setResearcheridNumber("");
        id = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

   
    /* @Test
    @DisplayName("Save researcher")
    public void saveTest(){
        BeanUtils.copyProperties(researcherDto, researcher);

        when(researcherService.save(researcheridNumber)).thenReturn(researcher);

        Researcher savedResearcher = researcherService.save(researcherDto);

        assertEquals(savedResearcher, researcher);
    }*/
    
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
    

    @Test
    @DisplayName("Delete researcher from database by ID")
    public void deleteTest() {
        UUID testeid = new UUID(0,0);
        when(researcherRepository.existsById(testeid)).thenReturn(true);
        researcherService.delete(id);
    }

    @Test
    @DisplayName("Update researcher at database")
    public void updateTest(){
        researcherDto.setId(id);
        doReturn(Optional.of(new Researcher())).when(researcherRepository).findById(id);
        when(researcherService.update(researcherDto)).thenReturn(researcher);
        var savedResearcher = researcherService.update(researcherDto);
        assertEquals(researcher, savedResearcher);
    }
}
