package br.edu.femass.latteback.repositories;

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
            "WHERE (:type IS NULL OR LOWER(c.productionType) LIKE LOWER(concat('%', :type, '%'))) ")
    List<Collaboration> findByProductionType(@Param("type") String type);

    @Query("SELECT c FROM Collaboration c " +
            "JOIN c.firstAuthor a1 " +
            "JOIN c.secondAuthor a2 " +
            "WHERE (:type IS NULL OR LOWER(c.productionType) = LOWER(:type)) " +
            "AND (:researcherName IS NULL OR LOWER(a1.name) LIKE LOWER(concat('%', :researcherName, '%')) OR LOWER(a2.name) LIKE LOWER(concat('%', :researcherName, '%')))")
    List<Collaboration> filterCollaborations(@Param("type") String type,
                                             @Param("researcherName") String researcherName);


}
