package br.edu.femass.latteback.repositories.Implementations;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.repositories.InstituteCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
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

        Root<Institute> instituteRoot = cq.from(Institute.class);
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            Expression<String> nameLower = cb.lower(instituteRoot.get("name"));
            String nameSearch = name.toLowerCase();
            predicates.add(cb.like(nameLower, "%" + nameSearch + "%"));
        }

        if (acronym != null && !acronym.isEmpty()) {
            Expression<String> acronymLower = cb.lower(instituteRoot.get("acronym"));
            String acronymSearch = acronym.toLowerCase();
            predicates.add(cb.like(acronymLower, "%" + acronymSearch + "%"));
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
