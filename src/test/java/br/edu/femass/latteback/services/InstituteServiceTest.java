package br.edu.femass.latteback.services;

import br.edu.femass.latteback.utils.enums.InstituteField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;


import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.BeanUtils;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.repositories.IInstituteRepository;
import br.edu.femass.latteback.dto.InstituteDto;

@ExtendWith(MockitoExtension.class)
public class InstituteServiceTest {
    @Mock
    private IInstituteRepository instituteRepository;

    @InjectMocks
    private InstituteService instituteService;
    private static InstituteDto instituteDto = new InstituteDto();
    private static final Institute institute = new Institute();
    private static UUID id;

    @BeforeEach
    public void setup(){
        instituteDto = new InstituteDto();
        instituteDto.setName("Teste1");
        instituteDto.setAcronym("TST");
        id = UUID.fromString("00000000-0000-0000-0000-000000000000");//Treated as a valid ID, change if necessary
    }

    @Test
    @DisplayName("Save institute")
    public void saveTest(){
        BeanUtils.copyProperties(instituteDto, institute);

        when(instituteService.save(instituteDto)).thenReturn(institute);

        Institute savedInstitute = instituteService.save(instituteDto);

        assertEquals(savedInstitute, institute);
    }

    @Test
    @DisplayName("Save null institute")
    public void saveNullInstitute(){
        instituteDto = null;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> instituteService.save(instituteDto));
        String expectedMessage = "Objeto instituto nulo";
        String exceptionMessage = exception.getMessage();
        assertEquals(expectedMessage, exceptionMessage);
    }

    @Test
    @DisplayName("Get all saved institutes")
    public void getAllTest(){
        Institute institute2 = new Institute();
        institute2.setName("Test");
        institute2.setAcronym("TST2");

        when(instituteService.getAll()).thenReturn(List.of(institute,institute2));

        List<Institute> savedInstitutes = new ArrayList<>();
        savedInstitutes.add(institute);
        savedInstitutes.add(institute2);

        assertEquals(savedInstitutes, instituteService.getAll());
        assertThat(savedInstitutes).isNotNull();
        assertThat(savedInstitutes.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Get single institute from database by ID")
    public void getByIdTest(){
        id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        when(instituteRepository.findById(id)).thenReturn(Optional.of(institute));
        var savedInstitute = instituteService.getById(id);
        assertEquals(savedInstitute, institute);
    }

    @Test
    @DisplayName("Get single institute from database by null ID")
    public void getByNullIDTest(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> instituteService.getById(null));
        String expectedMessage = "ID procurado não pode ser nulo.";
        String exceptionMessage = exception.getMessage();
        assertEquals(expectedMessage, exceptionMessage);
    }

    @Test
    @DisplayName("Get single institute from database by ID (not found)")
    public void getByIDNotFoundTest(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> instituteService.getById(id));
        String expectedMessage = "Não há instituto cadastrado com esse id";
        String exceptionMessage = exception.getMessage();
        assertEquals(expectedMessage, exceptionMessage);
    }

    @Test
    @DisplayName("Delete institute from database by ID")
    public void deleteTest(){
        UUID testeid = new UUID(0,0);
        when(instituteRepository.existsById(testeid)).thenReturn(true);
        instituteService.delete(id);

    }

    @Test
    @DisplayName("Delete institute from database by ID (not found)")
    public void deleteNotFoundTest(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> instituteService.delete(null));
        String expectedMessage = "Não há instituto cadastrado com esse id";
        String exceptionMessage = exception.getMessage();
        assertEquals(expectedMessage, exceptionMessage);
    }

    @Test
    @DisplayName("Update institute at database")
    public void updateTest(){
        instituteDto.setId(id);
        doReturn(Optional.of(new Institute())).when(instituteRepository).findById(id);
        when(instituteService.update(instituteDto)).thenReturn(institute);
        var savedInstitute = instituteService.update(instituteDto);
        assertEquals(institute, savedInstitute);
    }

    @Test
    @DisplayName("Update institute at database (ID not found)")
    public void updateNotFoundTest(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> instituteService.update(instituteDto));
        String expectedMessage = "Instituto não encontrado";
        String exceptionMessage = exception.getMessage();
        assertEquals(expectedMessage, exceptionMessage);
    }
    @Test
    @DisplayName("Update institute at database (null institute given)")
    public void updateNullTest(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> instituteService.update(null));
        String expectedMessage = "Objeto instituto nulo";
        String exceptionMessage = exception.getMessage();
        assertEquals(expectedMessage, exceptionMessage);
    }

    @Test
    @DisplayName("Institute search filter by string")
    public void filterByTextTest(){
        String textSearch = "test";
        Institute institute2 = new Institute();
        institute2.setName("Test");
        institute2.setAcronym("TST2");

        when(instituteRepository.findByNameContainsIgnoreCaseOrAcronymContainsIgnoreCase(anyString(),anyString())).thenReturn(List.of(institute, institute2));

        List<Institute> filteredInstitutes = new ArrayList<>();
        filteredInstitutes.add(institute);
        filteredInstitutes.add(institute2);

        assertEquals(filteredInstitutes, instituteService.filterInstituteByTextSearch(textSearch, InstituteField.ALL));
        assertThat(filteredInstitutes).isNotNull();
        assertThat(filteredInstitutes.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Institute search filter by empty string")
    public void filterByEmptySearchTest(){
        Institute institute2 = new Institute();
        institute2.setName("Test");
        institute2.setAcronym("TST2");
        Institute institute3 = new Institute();

        when(instituteService.getAll()).thenReturn(List.of(institute,institute2,institute3));

        List<Institute> filteredInstitutes = new ArrayList<>();
        filteredInstitutes.add(institute);
        filteredInstitutes.add(institute2);
        filteredInstitutes.add(institute3);

        assertEquals(filteredInstitutes, instituteService.filterInstituteByTextSearch(null, InstituteField.ALL));
        assertThat(filteredInstitutes).isNotNull();
        assertThat(filteredInstitutes.size()).isEqualTo(3);
    }
}
