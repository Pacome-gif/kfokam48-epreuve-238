package cm.kfokam48.presence.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "etudiant")
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    protected Etudiant() {
    }

    public Etudiant(String nom, Promotion promotion) {
        this.nom = nom;
        this.promotion = promotion;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}
