package cm.kfokam48.presence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presence.domain.Relecture;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    List<Relecture> findByRelecteurIdOrderByAssigneeAtDesc(Long relecteurId);

    /** Tableau : [auteurId, moyenne des notes rendues sur ses exercices] (RG14). */
    @Query("select r.exercice.etudiant.id, avg(r.note) from Relecture r "
            + "where r.rendueAt is not null and r.exercice.session.promotion.id = :promotionId "
            + "group by r.exercice.etudiant.id")
    List<Object[]> moyenneParAuteur(@Param("promotionId") Long promotionId);

    /** Tableau : [relecteurId, relectures assignées non rendues] (Q16). */
    @Query("select r.relecteur.id, count(r) from Relecture r "
            + "where r.rendueAt is null and r.relecteur.promotion.id = :promotionId group by r.relecteur.id")
    List<Object[]> enAttenteParRelecteur(@Param("promotionId") Long promotionId);

    /** Nombre de relectures déjà assignées à chaque étudiant dans une séance : [relecteurId, nombre]. */
    @Query("select r.relecteur.id, count(r) from Relecture r where r.exercice.session.id = :sessionId "
            + "group by r.relecteur.id")
    List<Object[]> compterParRelecteur(@Param("sessionId") Long sessionId);
}
