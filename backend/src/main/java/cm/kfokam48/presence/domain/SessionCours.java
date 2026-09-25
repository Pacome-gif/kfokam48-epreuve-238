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
import jakarta.persistence.Table;

@Entity
@Table(name = "session_cours")
public class SessionCours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @Column(name = "ouverture_at", nullable = false)
    private Instant ouvertureAt;

    @Column(name = "expiration_at", nullable = false)
    private Instant expirationAt;

    @Column(name = "cloture_at")
    private Instant clotureAt;

    protected SessionCours() {
    }

    public SessionCours(String titre, Promotion promotion, String code, Instant ouvertureAt, Instant expirationAt) {
        this.titre = titre;
        this.promotion = promotion;
        this.code = code;
        this.ouvertureAt = ouvertureAt;
        this.expirationAt = expirationAt;
    }

    /** RG1 : le code ne marche plus après l'expiration. */
    public boolean codeExpire(Instant maintenant) {
        return maintenant.isAfter(expirationAt);
    }

    /** H6 : la clôture est une action explicite du formateur. */
    public boolean estCloturee() {
        return clotureAt != null;
    }

    public void cloturer(Instant maintenant) {
        this.clotureAt = maintenant;
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public String getCode() {
        return code;
    }

    public Instant getOuvertureAt() {
        return ouvertureAt;
    }

    public Instant getExpirationAt() {
        return expirationAt;
    }

    public Instant getClotureAt() {
        return clotureAt;
    }
}
