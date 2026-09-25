package cm.kfokam48.presence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presence.domain.Relecture;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    /** Nombre de relectures déjà assignées à chaque étudiant dans une séance : [relecteurId, nombre]. */
    @Query("select r.relecteur.id, count(r) from Relecture r where r.exercice.session.id = :sessionId "
            + "group by r.relecteur.id")
    List<Object[]> compterParRelecteur(@Param("sessionId") Long sessionId);
}
