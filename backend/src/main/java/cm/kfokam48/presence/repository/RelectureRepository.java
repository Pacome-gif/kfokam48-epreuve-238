package cm.kfokam48.presence.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presence.domain.Relecture;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    List<Relecture> findByRelecteurIdOrderByAssigneeAtDesc(Long relecteurId);

    List<Relecture> findByExerciceId(Long exerciceId);

    long countByExerciceIdAndRendueAtIsNotNull(Long exerciceId);

    /** Tableau (RG14 v2, RG21) : relectures rendues sur les exercices d'une promotion. */
    @Query("select r from Relecture r join fetch r.exercice e "
            + "where r.rendueAt is not null and e.session.promotion.id = :promotionId")
    List<Relecture> renduesDeLaPromotion(@Param("promotionId") Long promotionId);

    /** EF12 : relectures rendues sur des exercices donnés. */
    List<Relecture> findByExerciceIdInAndRendueAtIsNotNullOrderByRendueAtAsc(Collection<Long> exerciceIds);

    /** Tableau : [relecteurId, relectures assignées non rendues] (Q16). */
    @Query("select r.relecteur.id, count(r) from Relecture r "
            + "where r.rendueAt is null and r.relecteur.promotion.id = :promotionId group by r.relecteur.id")
    List<Object[]> enAttenteParRelecteur(@Param("promotionId") Long promotionId);

    /** Nombre de relectures déjà assignées à chaque étudiant dans une séance : [relecteurId, nombre]. */
    @Query("select r.relecteur.id, count(r) from Relecture r where r.exercice.session.id = :sessionId "
            + "group by r.relecteur.id")
    List<Object[]> compterParRelecteur(@Param("sessionId") Long sessionId);
}
