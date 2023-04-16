package br.edu.femass.latteback.repositories.Implementations;

import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.repositories.InstituteRepository;
import br.edu.femass.latteback.repositories.ResearcherCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class ResearcherCustomRepositoryImpl implements ResearcherCustomRepository {
    @PersistenceContext
    EntityManager em;

    private  final InstituteRepository instituteRepository;

    public ResearcherCustomRepositoryImpl(InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }


    @Override
    public Page<Researcher> AdvancedSearch(String name, String institueAcronym, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Researcher> cq = cb.createQuery(Researcher.class);

        Root<Researcher> researcherRoot = cq.from(Researcher.class);
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(researcherRoot.get("name"), "%" + name + "%"));
        }

        if (institueAcronym != null && !institueAcronym.isEmpty()) {
            Join<Researcher, Institute> join = researcherRoot.join("institute");
            predicates.add(cb.like(join.get("acronym"), "%" + institueAcronym + "%"));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[]{}));
        }

        TypedQuery<Researcher> query = em.createQuery(cq);

        List<Researcher> result = query.getResultList();

        int total = result.size();
        PagedListHolder<Researcher> p = new PagedListHolder<>(result);
        p.setPageSize(pageable.getPageSize());
        p.setPage(pageable.getPageNumber());

        return new PageImpl<>(p.getPageList(), pageable, total);
    }
}
