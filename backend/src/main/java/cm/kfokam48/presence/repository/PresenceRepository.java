package cm.kfokam48.presence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presence.domain.Presence;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    @Query("select p.etudiant.id from Presence p where p.session.id = :sessionId")
    List<Long> idsPresents(@Param("sessionId") Long sessionId);
}
