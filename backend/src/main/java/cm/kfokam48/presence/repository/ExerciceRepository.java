package cm.kfokam48.presence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presence.domain.Exercice;
import cm.kfokam48.presence.domain.StatutExercice;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Exercice> findBySessionIdAndStatutOrderByDeposeAtAsc(Long sessionId, StatutExercice statut);

    /** Tableau : [etudiantId, exercices déposés, exercices pas encore relus (RG17)] pour une promotion. */
    @Query("select e.etudiant.id, count(e), "
            + "sum(case when e.statut <> cm.kfokam48.presence.domain.StatutExercice.RELU then 1 else 0 end) "
            + "from Exercice e where e.session.promotion.id = :promotionId group by e.etudiant.id")
    List<Object[]> compterParEtudiant(@Param("promotionId") Long promotionId);
}
