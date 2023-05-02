package br.edu.femass.latteback.repositories.Implementations;

import br.edu.femass.latteback.models.Book;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.repositories.BookCustomRepository;
import br.edu.femass.latteback.repositories.InstituteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookCustomRepositoryImpl implements BookCustomRepository {

    @PersistenceContext
    EntityManager em;

    public BookCustomRepositoryImpl() { }

    @Override
    public Page<Book> AdvancedSearch(String title, LocalDate startDate, LocalDate endDate, UUID researcherId, UUID instituteId, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);

        Root<Book> bookRoot = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            predicates.add(cb.like(bookRoot.get("title"), "%" + title + "%"));
        }

        if(startDate != null && endDate == null) {
            predicates.add(cb.equal(bookRoot.get("year"), String.valueOf(startDate.getYear())));
        } else if(startDate == null && endDate != null) {
            predicates.add(cb.equal(bookRoot.get("year"), String.valueOf(endDate.getYear())));
        } else {
            predicates.add(cb.or(
                    cb.equal(bookRoot.get("year"), String.valueOf(startDate.getYear())),
                    cb.equal(bookRoot.get("year"), String.valueOf(endDate.getYear()))
            ));
        }

        if (researcherId != null) {
            Join<Book, Researcher> join = bookRoot.join("researcher");
            predicates.add(cb.equal(join.get("id"), researcherId));
        }

        if (instituteId != null) {
            Join<Researcher, Institute> firstJoin = bookRoot.join("institute");
            predicates.add(cb.equal(firstJoin.get("id"), instituteId));

        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[]{}));
        }

        TypedQuery<Book> query = em.createQuery(cq);

        List<Book> result = query.getResultList();

        int total = result.size();
        PagedListHolder<Book> p = new PagedListHolder<>(result);
        p.setPageSize(pageable.getPageSize());
        p.setPage(pageable.getPageNumber());

        return new PageImpl<>(p.getPageList(), pageable, total);
    }

    @Override
    public Page<Book> AdvancedSearch(String title, LocalDate startDate, LocalDate endDate, UUID researcherId, Pageable pageable) {
        return AdvancedSearch(title, startDate, endDate, researcherId, null, pageable);
    }
}
