package cm.kfokam48.presence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import cm.kfokam48.presence.domain.Etudiant;
import cm.kfokam48.presence.domain.Presence;
import cm.kfokam48.presence.domain.Promotion;
import cm.kfokam48.presence.domain.SessionCours;
import cm.kfokam48.presence.domain.SourcePresence;
import cm.kfokam48.presence.exception.CodeErreur;
import cm.kfokam48.presence.exception.MetierException;
import cm.kfokam48.presence.repository.EtudiantRepository;
import cm.kfokam48.presence.repository.PresenceRepository;
import cm.kfokam48.presence.repository.SessionRepository;
import cm.kfokam48.presence.service.PresenceService;
import cm.kfokam48.presence.service.SessionService;

/**
 * Test unitaire de la règle RG1 (Q2) : le code expire 15 minutes après l'ouverture, pas avant.
 * L'horloge est figée : aucun temps d'attente, aucune base.
 */
class PresenceServiceTest {

    static final Instant OUVERTURE = Instant.parse("2026-09-25T08:00:00Z");

    SessionRepository sessions = mock(SessionRepository.class);
    EtudiantRepository etudiants = mock(EtudiantRepository.class);
    PresenceRepository presences = mock(PresenceRepository.class);
    Etudiant awa;

    @BeforeEach
    void donnees() {
        Promotion promo = new Promotion("Promo");
        ReflectionTestUtils.setField(promo, "id", 1L);
        awa = new Etudiant("Awa", promo);
        ReflectionTestUtils.setField(awa, "id", 10L);
        SessionCours session = new SessionCours("S1", promo, "ABC234", OUVERTURE,
                OUVERTURE.plus(SessionService.VALIDITE_CODE));
        ReflectionTestUtils.setField(session, "id", 100L);

        when(etudiants.findById(10L)).thenReturn(Optional.of(awa));
        when(sessions.findByCode("ABC234")).thenReturn(Optional.of(session));
        when(presences.saveAndFlush(any(Presence.class))).thenAnswer(i -> i.getArgument(0));
    }

    PresenceService serviceA(String instant) {
        return new PresenceService(sessions, etudiants, presences,
                Clock.fixed(Instant.parse(instant), ZoneOffset.UTC));
    }

    @Test
    void codeAccepteJusquaLaQuinziemeMinuteIncluse() {
        var presence = serviceA("2026-09-25T08:15:00Z").marquer("abc234", 10L);

        assertThat(presence.source()).isEqualTo(SourcePresence.ETUDIANT);
        assertThat(presence.sessionId()).isEqualTo(100L);
    }

    @Test
    void codeRefuseUneSecondeApresLesQuinzeMinutes() {
        assertThatThrownBy(() -> serviceA("2026-09-25T08:15:01Z").marquer("ABC234", 10L))
                .isInstanceOf(MetierException.class)
                .extracting("code").isEqualTo(CodeErreur.CODE_EXPIRE);
        verify(presences, never()).saveAndFlush(any());
    }

    @Test
    void secondePresenceRefusee() {
        when(presences.existsBySessionIdAndEtudiantId(100L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> serviceA("2026-09-25T08:05:00Z").marquer("ABC234", 10L))
                .extracting("code").isEqualTo(CodeErreur.DEJA_PRESENT);
    }
}
