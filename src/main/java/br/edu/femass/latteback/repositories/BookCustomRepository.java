package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface BookCustomRepository {
    Page<Book> AdvancedSearch(String title, LocalDate startDate, LocalDate endDate, UUID researcherId, UUID instituteId, Pageable pageable);
    Page<Book> AdvancedSearch(String title, LocalDate startDate, LocalDate endDate, UUID researcherId, Pageable pageable);

}
