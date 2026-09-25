package cm.kfokam48.presence.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presence.domain.Exercice;
import cm.kfokam48.presence.domain.Relecture;
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
        Map<Long, NoteRetenue> moyennes = moyennesParEtudiant(promotionId);

        return etudiants.findByPromotionIdOrderByNomAsc(promotionId).stream()
                .map(e -> new LigneTableauDto(e.getId(), e.getNom(),
                        nbPresences.getOrDefault(e.getId(), 0L),
                        nbDeposes.getOrDefault(e.getId(), 0L),
                        moyennes.getOrDefault(e.getId(), NoteRetenue.AUCUNE).note(), // null si aucune (RG14)
                        moyennes.getOrDefault(e.getId(), NoteRetenue.AUCUNE).provisoire(),
                        nbAFaire.getOrDefault(e.getId(), 0L),
                        nbNonRelus.getOrDefault(e.getId(), 0L)))
                .toList();
    }

    /** RG21 par exercice, puis RG14 v2 par étudiant. */
    private Map<Long, NoteRetenue> moyennesParEtudiant(Long promotionId) {
        Map<Exercice, List<Integer>> notesParExercice = relectures.renduesDeLaPromotion(promotionId).stream()
                .collect(Collectors.groupingBy(Relecture::getExercice,
                        Collectors.mapping(Relecture::getNote, Collectors.toList())));
        Map<Long, List<NoteRetenue>> notesParEtudiant = new HashMap<>();
        notesParExercice.forEach((exercice, notes) -> notesParEtudiant
                .computeIfAbsent(exercice.getEtudiant().getId(), id -> new ArrayList<>())
                .add(NoteRetenue.calculer(notes, exercice.getRelecteursRequis())));
        Map<Long, NoteRetenue> moyennes = new HashMap<>();
        notesParEtudiant.forEach((etudiantId, notes) -> moyennes.put(etudiantId, NoteRetenue.moyenne(notes)));
        return moyennes;
    }

    private static Map<Long, Long> parEtudiant(List<Object[]> lignes, int colonne) {
        Map<Long, Long> resultat = new HashMap<>();
        for (Object[] ligne : lignes) {
            resultat.put((Long) ligne[0], ((Number) ligne[colonne]).longValue());
        }
        return resultat;
    }
}
