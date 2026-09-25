package cm.kfokam48.presence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presence.domain.Exercice;
import jakarta.persistence.LockModeType;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    /** H1, H12 : exercices de la séance à qui il manque encore des relecteurs. */
    @Query("select e from Exercice e where e.session.id = :sessionId "
            + "and e.statut <> cm.kfokam48.presence.domain.StatutExercice.RELU "
            + "and (select count(r) from Relecture r where r.exercice = e) < e.relecteursRequis "
            + "order by e.deposeAt")
    List<Exercice> manquantDeRelecteurs(@Param("sessionId") Long sessionId);

    /** H14 : verrou pendant l'attribution, pour ne jamais dépasser le nombre de relecteurs requis. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Exercice e where e.id = :id")
    Optional<Exercice> verrouiller(@Param("id") Long id);

    /** Tableau : [etudiantId, exercices déposés, exercices pas encore relus (RG17)] pour une promotion. */
    @Query("select e.etudiant.id, count(e), "
            + "sum(case when e.statut <> cm.kfokam48.presence.domain.StatutExercice.RELU then 1 else 0 end) "
            + "from Exercice e where e.session.promotion.id = :promotionId group by e.etudiant.id")
    List<Object[]> compterParEtudiant(@Param("promotionId") Long promotionId);
}
