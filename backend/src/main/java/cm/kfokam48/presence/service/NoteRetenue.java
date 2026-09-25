package cm.kfokam48.presence.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

/**
 * RG21 : note retenue d'un exercice = moyenne des notes rendues, provisoire tant que toutes les relectures
 * requises ne sont pas rendues. RG14 v2 : moyenne d'un étudiant = moyenne de ses notes retenues.
 * Règle pure, sans base : c'est ici, et nulle part ailleurs (ni dans le frontend), que la note est calculée.
 */
public record NoteRetenue(Double note, boolean provisoire) {

    public static final NoteRetenue AUCUNE = new NoteRetenue(null, false);

    /** @param notesRendues notes entières (RG8) des relectures rendues de l'exercice */
    public static NoteRetenue calculer(Collection<Integer> notesRendues, int relecteursRequis) {
        if (notesRendues.isEmpty()) {
            return AUCUNE;
        }
        double moyenne = notesRendues.stream().mapToInt(Integer::intValue).average().orElseThrow();
        return new NoteRetenue(arrondi(moyenne), notesRendues.size() < relecteursRequis);
    }

    /** Moyenne des notes retenues existantes ; provisoire si l'une d'elles l'est. */
    public static NoteRetenue moyenne(List<NoteRetenue> notes) {
        List<NoteRetenue> notees = notes.stream().filter(n -> n.note() != null).toList();
        if (notees.isEmpty()) {
            return AUCUNE;
        }
        double moyenne = notees.stream().mapToDouble(NoteRetenue::note).average().orElseThrow();
        return new NoteRetenue(arrondi(moyenne), notees.stream().anyMatch(NoteRetenue::provisoire));
    }

    static double arrondi(double valeur) {
        return BigDecimal.valueOf(valeur).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
