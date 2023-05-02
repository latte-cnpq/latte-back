package br.edu.femass.latteback.repositories;

import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.models.Researcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, BookCustomRepository {
    List<Book> findByResearcher(Researcher researcher);
}