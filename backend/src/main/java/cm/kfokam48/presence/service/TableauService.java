package cm.kfokam48.presence.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.dto.LigneTableauDto;
import cm.kfokam48.presence.repository.EtudiantRepository;
import cm.kfokam48.presence.repository.ExerciceRepository;
import cm.kfokam48.presence.repository.PresenceRepository;
import cm.kfokam48.presence.repository.RelectureRepository;

/**
 * EF6 : tableau de suivi par étudiant. Quatre requêtes agrégées, quelle que soit la taille de la promotion.
 */
@Service
@Transactional(readOnly = true)
public class TableauService {

    private final ReferentielService referentiel;
    private final EtudiantRepository etudiants;
    private final PresenceRepository presences;
    private final ExerciceRepository exercices;
    private final RelectureRepository relectures;

    public TableauService(ReferentielService referentiel, EtudiantRepository etudiants,
            PresenceRepository presences, ExerciceRepository exercices, RelectureRepository relectures) {
        this.referentiel = referentiel;
        this.etudiants = etudiants;
        this.presences = presences;
        this.exercices = exercices;
        this.relectures = relectures;
    }

    public List<LigneTableauDto> tableau(Long promotionId) {
        referentiel.promotion(promotionId); // 404 PROMOTION_INCONNUE
        Map<Long, Long> nbPresences = parEtudiant(presences.compterParEtudiant(promotionId), 1);
        List<Object[]> statsExercices = exercices.compterParEtudiant(promotionId);
        Map<Long, Long> nbDeposes = parEtudiant(statsExercices, 1);
        Map<Long, Long> nbNonRelus = parEtudiant(statsExercices, 2);
        Map<Long, Long> nbAFaire = parEtudiant(relectures.enAttenteParRelecteur(promotionId), 1);
        Map<Long, Double> moyennes = new HashMap<>();
        for (Object[] ligne : relectures.moyenneParAuteur(promotionId)) {
            moyennes.put((Long) ligne[0], arrondi(((Number) ligne[1]).doubleValue()));
        }

        return etudiants.findByPromotionIdOrderByNomAsc(promotionId).stream()
                .map(e -> new LigneTableauDto(e.getId(), e.getNom(),
                        nbPresences.getOrDefault(e.getId(), 0L),
                        nbDeposes.getOrDefault(e.getId(), 0L),
                        moyennes.get(e.getId()), // null si aucune note (RG14)
                        nbAFaire.getOrDefault(e.getId(), 0L),
                        nbNonRelus.getOrDefault(e.getId(), 0L)))
                .toList();
    }

    /** RG14 : moyenne arrondie à 2 décimales. */
    static Double arrondi(double moyenne) {
        return BigDecimal.valueOf(moyenne).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static Map<Long, Long> parEtudiant(List<Object[]> lignes, int colonne) {
        Map<Long, Long> resultat = new HashMap<>();
        for (Object[] ligne : lignes) {
            resultat.put((Long) ligne[0], ((Number) ligne[colonne]).longValue());
        }
        return resultat;
    }
}
