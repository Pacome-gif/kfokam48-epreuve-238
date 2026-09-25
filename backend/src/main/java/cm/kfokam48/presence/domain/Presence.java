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
@Table(name = "presence")
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCours session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SourcePresence source;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Presence() {
    }

    public Presence(SessionCours session, Etudiant etudiant, SourcePresence source, Instant createdAt) {
        this.session = session;
        this.etudiant = etudiant;
        this.source = source;
        this.createdAt = createdAt;
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

    public SourcePresence getSource() {
        return source;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
