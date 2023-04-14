package br.edu.femass.latteback.repositories.Implementations;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.repositories.InstituteCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InstituteCustomRepositoryImpl implements InstituteCustomRepository {
    @PersistenceContext
    EntityManager em;

    @Override
    public Page<Institute> AdvancedSearch(String name, String acronym, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Institute> cq = cb.createQuery(Institute.class);

        Root<Institute> Institute = cq.from(Institute.class);
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.equal(Institute.get("name"), name));
        }

        if (acronym != null && !acronym.isEmpty()) {
            predicates.add(cb.equal(Institute.get("acronym"), acronym));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[]{}));
        }

        TypedQuery<Institute> query = em.createQuery(cq);

        List<Institute> result = query.getResultList();

        int total = result.size();
        PagedListHolder<Institute> p = new PagedListHolder<>(result);
        p.setPageSize(pageable.getPageSize());
        p.setPage(pageable.getPageNumber());

        return new PageImpl<>(p.getPageList(), pageable, total);
    }

}
