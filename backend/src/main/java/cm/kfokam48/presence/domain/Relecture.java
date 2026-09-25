package cm.kfokam48.presence.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "relecture")
public class Relecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** RG6 : un seul relecteur par exercice (contrainte unique en base). */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercice_id", nullable = false, unique = true)
    private Exercice exercice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relecteur_id", nullable = false)
    private Etudiant relecteur;

    private Integer note;

    @Column(length = 2000)
    private String commentaire;

    @Column(name = "assignee_at", nullable = false)
    private Instant assigneeAt;

    @Column(name = "rendue_at")
    private Instant rendueAt;

    protected Relecture() {
    }

    public Relecture(Exercice exercice, Etudiant relecteur, Instant assigneeAt) {
        this.exercice = exercice;
        this.relecteur = relecteur;
        this.assigneeAt = assigneeAt;
    }

    public boolean estRendue() {
        return rendueAt != null;
    }

    public void rendre(int note, String commentaire, Instant maintenant) {
        this.note = note;
        this.commentaire = commentaire;
        this.rendueAt = maintenant;
        exercice.marquerRelu();
    }

    public Long getId() {
        return id;
    }

    public Exercice getExercice() {
        return exercice;
    }

    public Etudiant getRelecteur() {
        return relecteur;
    }

    public Integer getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public Instant getAssigneeAt() {
        return assigneeAt;
    }

    public Instant getRendueAt() {
        return rendueAt;
    }
}
