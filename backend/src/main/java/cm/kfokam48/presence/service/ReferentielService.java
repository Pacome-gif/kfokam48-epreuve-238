package cm.kfokam48.presence.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.domain.Etudiant;
import cm.kfokam48.presence.domain.Promotion;
import cm.kfokam48.presence.dto.EtudiantDto;
import cm.kfokam48.presence.dto.PromotionDto;
import cm.kfokam48.presence.exception.CodeErreur;
import cm.kfokam48.presence.exception.MetierException;
import cm.kfokam48.presence.repository.EtudiantRepository;
import cm.kfokam48.presence.repository.PromotionRepository;

/**
 * Promotions et étudiants : lecture seule, les données viennent de la migration de démonstration.
 */
@Service
@Transactional(readOnly = true)
public class ReferentielService {

    private final PromotionRepository promotions;
    private final EtudiantRepository etudiants;

    public ReferentielService(PromotionRepository promotions, EtudiantRepository etudiants) {
        this.promotions = promotions;
        this.etudiants = etudiants;
    }

    public List<PromotionDto> listerPromotions() {
        return promotions.findAllByOrderByNomAsc().stream()
                .map(p -> new PromotionDto(p.getId(), p.getNom()))
                .toList();
    }

    public List<EtudiantDto> listerEtudiants(Long promotionId) {
        promotion(promotionId);
        return etudiants.findByPromotionIdOrderByNomAsc(promotionId).stream()
                .map(e -> new EtudiantDto(e.getId(), e.getNom(), promotionId))
                .toList();
    }

    public Promotion promotion(Long id) {
        return promotions.findById(id).orElseThrow(() -> new MetierException(CodeErreur.PROMOTION_INCONNUE));
    }

    public Etudiant etudiant(Long id) {
        return etudiants.findById(id).orElseThrow(() -> new MetierException(CodeErreur.ETUDIANT_INCONNU));
    }
}
