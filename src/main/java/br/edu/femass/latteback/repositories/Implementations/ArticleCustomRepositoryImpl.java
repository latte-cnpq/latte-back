package br.edu.femass.latteback.repositories.Implementations;

import br.edu.femass.latteback.models.Article;
import br.edu.femass.latteback.models.Institute;
import br.edu.femass.latteback.models.Researcher;
import br.edu.femass.latteback.repositories.ArticleCustomRepository;
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

public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {
    @PersistenceContext
    EntityManager em;

    public ArticleCustomRepositoryImpl() { }

    @Override
    public Page<Article> AdvancedSearch(String title, LocalDate startDate, LocalDate endDate, String researcherName, String instituteName, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Article> cq = cb.createQuery(Article.class);

        Root<Article> articleRoot = cq.from(Article.class);
        List<Predicate> predicates = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            predicates.add(cb.like(articleRoot.get("title"), "%" + title + "%"));
        }

        if(startDate != null && endDate == null) {
            predicates.add(cb.equal(articleRoot.get("year"), String.valueOf(startDate.getYear())));
        } else if(startDate == null && endDate != null) {
            predicates.add(cb.equal(articleRoot.get("year"), String.valueOf(endDate.getYear())));
        } else if(startDate != null && endDate != null) {
            predicates.add(cb.or(
                cb.equal(articleRoot.get("year"), String.valueOf(startDate.getYear())),
                cb.equal(articleRoot.get("year"), String.valueOf(endDate.getYear()))
            ));
        }

        if (researcherName != null) {
            Join<Article, Researcher> join = articleRoot.join("researcher");
            predicates.add(cb.like(join.get("name"), "%" + researcherName + "%"));
        }

        if (instituteName != null) {
            Join<Researcher, Institute> firstJoin = articleRoot.join("institute");
            predicates.add(cb.like(firstJoin.get("name"), "%" + instituteName + "%"));

        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[]{}));
        }

        TypedQuery<Article> query = em.createQuery(cq);

        List<Article> result = query.getResultList();

        int total = result.size();
        PagedListHolder<Article> p = new PagedListHolder<>(result);
        p.setPageSize(pageable.getPageSize());
        p.setPage(pageable.getPageNumber());

        return new PageImpl<>(p.getPageList(), pageable, total);
    }

    @Override
    public Page<Article> AdvancedSearch(String title, LocalDate startDate, LocalDate endDate, String researcherName, Pageable pageable) {
        return AdvancedSearch(title, startDate, endDate, researcherName, null, pageable);
    }
}
