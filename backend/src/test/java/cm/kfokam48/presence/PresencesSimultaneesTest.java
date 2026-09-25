package cm.kfokam48.presence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import cm.kfokam48.presence.domain.StatutExercice;
import cm.kfokam48.presence.dto.ExerciceCreeDto;
import cm.kfokam48.presence.dto.ExerciceDepotDto;
import cm.kfokam48.presence.dto.PresenceDto;
import cm.kfokam48.presence.dto.SessionCreationDto;
import cm.kfokam48.presence.dto.SessionOuverteDto;
import cm.kfokam48.presence.repository.ExerciceRepository;
import cm.kfokam48.presence.repository.PresenceRepository;
import cm.kfokam48.presence.service.ExerciceService;
import cm.kfokam48.presence.service.PresenceService;
import cm.kfokam48.presence.service.SessionService;

/**
 * Bug #21 : deux étudiants qui marquent leur présence « presque en même temps » — une présence est perdue.
 *
 * Reproduction déterministe de l'entrelacement observé en production :
 * un exercice attend un relecteur ; la présence de A est enregistrée mais sa transaction n'est pas encore
 * validée quand la présence de B arrive. Les deux tentent d'attribuer le même exercice.
 * Attendu : les deux présences existent, l'exercice a un relecteur.
 */
@SpringBootTest
class PresencesSimultaneesTest {

    static final long AUTEUR = 1, ETUDIANT_A = 2, ETUDIANT_B = 3;

    @Autowired
    SessionService sessions;
    @Autowired
    ExerciceService exercices;
    @Autowired
    PresenceService presences;
    @Autowired
    PresenceRepository presenceRepository;
    @Autowired
    ExerciceRepository exerciceRepository;
    @Autowired
    TransactionTemplate transaction;

    ExecutorService threads = Executors.newFixedThreadPool(2);

    @AfterEach
    void arreter() {
        threads.shutdownNow();
    }

    @Test
    void deuxPresencesSimultaneesSontToutesDeuxEnregistrees() throws Exception {
        SessionOuverteDto seance = sessions.ouvrir(new SessionCreationDto("Bug #21", 1L));
        ExerciceCreeDto exercice = exercices.deposer(
                new ExerciceDepotDto(seance.id(), AUTEUR, "https://github.com/awa/bug21"));
        assertThat(exercice.statut()).isEqualTo(StatutExercice.DEPOSE); // personne n'est présent

        CountDownLatch presenceAEcrite = new CountDownLatch(1);
        CountDownLatch validerA = new CountDownLatch(1);

        // Étudiant A : sa présence est écrite, mais sa transaction reste ouverte un instant
        Future<PresenceDto> a = threads.submit(() -> transaction.execute(statut -> {
            PresenceDto p = presences.marquer(seance.code(), ETUDIANT_A);
            presenceAEcrite.countDown();
            attendre(validerA);
            return p;
        }));
        assertThat(presenceAEcrite.await(5, TimeUnit.SECONDS)).isTrue();

        // Étudiant B tape le code pendant ce temps
        Future<PresenceDto> b = threads.submit(() -> presences.marquer(seance.code(), ETUDIANT_B));
        Thread.sleep(300);
        validerA.countDown();

        PresenceDto presenceA = a.get(15, TimeUnit.SECONDS);
        PresenceDto presenceB = b.get(15, TimeUnit.SECONDS); // avant correctif : ExecutionException (500)

        assertThat(presenceA).isNotNull();
        assertThat(presenceB).isNotNull();
        assertThat(presenceRepository.existsBySessionIdAndEtudiantId(seance.id(), ETUDIANT_A)).isTrue();
        assertThat(presenceRepository.existsBySessionIdAndEtudiantId(seance.id(), ETUDIANT_B)).isTrue();
        assertThat(exerciceRepository.findById(exercice.id()).orElseThrow().getStatut())
                .isEqualTo(StatutExercice.EN_ATTENTE_RELECTURE);
    }

    private static void attendre(CountDownLatch verrou) {
        try {
            verrou.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
