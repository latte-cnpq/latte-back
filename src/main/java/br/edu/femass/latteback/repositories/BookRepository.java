package br.edu.femass.latteback.repositories;
import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.utils.enums.ProductionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findByResearcher(UUID researcherId);
    List<Book> findByInstitute(UUID instituteId);
    List<Book> findByCriteria(String title, UUID researcherId, UUID instituteId, LocalDate startDate, LocalDate endDate, ProductionType type);
}