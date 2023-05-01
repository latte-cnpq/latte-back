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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.models.ResearcherCache;
import br.edu.femass.latteback.repositories.ArticleRepositoy;
import br.edu.femass.latteback.repositories.BookRepository;
import br.edu.femass.latteback.repositories.ResearcherCacheRepository;
import br.edu.femass.latteback.repositories.ResearcherRepository;

@ExtendWith(MockitoExtension.class)
public class ResearcherServiceTest {
    @Mock
    private ResearcherRepository researcherRepository;

    @Mock
    private ResearcherCacheRepository researcherCacheRepository;

    @Mock
    private ArticleRepositoy articleRepositoy;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ResearcherService researcherService;
    private static final Researcher researcher = new Researcher();
    private static UUID id;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Save researchers")
    public void saveTest(){
        //given
        Institute institute = new Institute();
        institute.setName("Institute1");
        institute.setAcronym("IST1");
        institute.setId(UUID.randomUUID());
        
        Article article = new Article();
        article.setTitle("Titulo");
        article.setPublishedOn("2023");
        article.setVolume("2");
        article.setPages("100");
        article.setYear("2023");
        //article.setAuthorNames("Pesquisador");
        article.setResearcher(researcher);

        Book book = new Book();
        book.setTitle("Titulo");
        book.setPublisher("2023");
        book.setVolume("2");
        book.setPages("100");
        book.setYear("2023");
        //book.setAuthorNames("Pesquisador");
        book.setResearcher(researcher);

        ResearcherCache researcherCache = new ResearcherCache("Pesquisador", "0023809873085852", "0023809873085852");

        when(researcherService.save("0023809873085852", institute.getId())).thenReturn(researcher);
        when(researcherCacheRepository.findFirstByResearcherIdNumber("0023809873085852")).thenReturn(Optional.of(researcherCache));
        when(researcherRepository.save(researcher)).thenReturn(researcher);
        when(articleRepositoy.save(article)).thenReturn(article);
        when(bookRepository.save(book)).thenReturn(book);
        //when
        Researcher savedResearcher = researcherService.save("0023809873085852", institute.getId());
        
        //then
        assertEquals(savedResearcher, researcher);
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
    

    @Test
    @DisplayName("Delete researcher from database by ID")
    public void deleteTest() {
        UUID testeid = new UUID(0,0);
        when(researcherRepository.existsById(testeid)).thenReturn(true);
        researcherService.delete(id);
    }


}
