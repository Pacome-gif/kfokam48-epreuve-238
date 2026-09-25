package cm.kfokam48.presence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cm.kfokam48.presence.domain.SessionCours;

public interface SessionRepository extends JpaRepository<SessionCours, Long> {

    Optional<SessionCours> findByCode(String code);

    boolean existsByCode(String code);

    List<SessionCours> findByPromotionIdOrderByOuvertureAtDesc(Long promotionId);
}
