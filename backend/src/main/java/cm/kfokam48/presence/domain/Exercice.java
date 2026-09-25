package cm.kfokam48.presence.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "exercice")
public class Exercice {

    /** RG6 v2 : deux relecteurs distincts pour tout nouveau dépôt. */
    public static final int RELECTEURS_REQUIS = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCours session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Column(nullable = false, length = 500)
    private String lien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatutExercice statut;

    @Column(name = "depose_at", nullable = false)
    private Instant deposeAt;

    /** 2 ; 1 pour les exercices déposés avant la migration V3 (H11). */
    @Column(name = "relecteurs_requis", nullable = false)
    private int relecteursRequis;

    protected Exercice() {
    }

    public Exercice(SessionCours session, Etudiant etudiant, String lien, Instant deposeAt) {
        this.session = session;
        this.etudiant = etudiant;
        this.lien = lien;
        this.deposeAt = deposeAt;
        this.statut = StatutExercice.DEPOSE;
        this.relecteursRequis = RELECTEURS_REQUIS;
    }

    /** D4 : DEPOSE -> EN_ATTENTE_RELECTURE quand un relecteur est tiré. */
    public void mettreEnAttenteDeRelecture() {
        this.statut = StatutExercice.EN_ATTENTE_RELECTURE;
    }

    /** D4 : EN_ATTENTE_RELECTURE -> RELU quand toutes les relectures requises sont rendues (RG20). */
    public void marquerRelu() {
        this.statut = StatutExercice.RELU;
    }

    public Long getId() {
        return id;
    }

    public SessionCours getSession() {
        return session;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public String getLien() {
        return lien;
    }

    public StatutExercice getStatut() {
        return statut;
    }

    public Instant getDeposeAt() {
        return deposeAt;
    }

    public int getRelecteursRequis() {
        return relecteursRequis;
    }
}
