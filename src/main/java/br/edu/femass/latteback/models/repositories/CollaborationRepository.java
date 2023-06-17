package br.edu.femass.latteback.models.repositories;

import br.edu.femass.latteback.models.graph.Collaboration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CollaborationRepository extends JpaRepository<Collaboration, UUID>  {
    List<Collaboration> findByProductionTitleIgnoreCase(String productionTitle);
    @Query("SELECT c FROM Collaboration c " +
            "JOIN c.firstAuthor r " +
            "JOIN r.institute i " +
            "WHERE i.name = :instituteName")
    List<Collaboration> findByInstituteName(@Param("instituteName") String instituteName);

    @Query("SELECT c FROM Collaboration c " +
            "JOIN c.firstAuthor a1 " +
            "JOIN c.secondAuthor a2 " +
            "JOIN a1.institute i1 " +
            "JOIN a2.institute i2 " +
            "WHERE (:instituteName IS NULL OR LOWER(i1.name) LIKE LOWER(concat('%', :instituteName, '%')) OR LOWER(i2.name) LIKE LOWER(concat('%', :instituteName, '%'))) " +
            "AND (:type IS NULL OR LOWER(c.productionType) = LOWER(:type)) " +
            "AND (:researcherName IS NULL OR LOWER(a1.name) LIKE LOWER(concat('%', :researcherName, '%')) OR LOWER(a2.name) LIKE LOWER(concat('%', :researcherName, '%')))")
    List<Collaboration> filterCollaborations(@Param("instituteName") String instituteName,
                                             @Param("type") String type,
                                             @Param("researcherName") String researcherName);


}
