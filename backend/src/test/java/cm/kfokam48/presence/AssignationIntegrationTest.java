package cm.kfokam48.presence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cm.kfokam48.presence.domain.StatutExercice;
import cm.kfokam48.presence.repository.ExerciceRepository;
import cm.kfokam48.presence.repository.RelectureRepository;

/** EF4 v2 : deux relecteurs distincts, complétés aux présences suivantes s'il en manque (H1, H12). */
@SpringBootTest
@AutoConfigureMockMvc
class AssignationIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    @Autowired
    ExerciceRepository exercices;

    @Autowired
    RelectureRepository relectures;

    JsonNode post(String url, String corps) throws Exception {
        return json.readTree(mvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andReturn().getResponse().getContentAsString());
    }

    List<Long> relecteurs(long exerciceId) {
        return relectures.findByExerciceId(exerciceId).stream().map(r -> r.getRelecteur().getId()).toList();
    }

    @Test
    void exerciceSansAutrePresentEstCompleteAuxPresencesSuivantes() throws Exception {
        JsonNode session = post("/api/sessions", "{\"titre\":\"Assignation\",\"promotionId\":1}");
        String code = session.get("code").asText();
        long sessionId = session.get("id").asLong();

        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":1}");
        JsonNode depot = post("/api/exercices",
                "{\"sessionId\":" + sessionId + ",\"etudiantId\":1,\"lien\":\"https://github.com/awa/exo\"}");
        long exerciceId = depot.get("id").asLong();
        assertThat(depot.get("statut").asText()).isEqualTo("DEPOSE");

        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":2}");
        assertThat(relecteurs(exerciceId)).containsExactly(2L);
        assertThat(exercices.findById(exerciceId).orElseThrow().getStatut())
                .isEqualTo(StatutExercice.EN_ATTENTE_RELECTURE);

        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":3}");
        assertThat(relecteurs(exerciceId)).containsExactlyInAnyOrder(2L, 3L);

        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":4}");
        assertThat(relecteurs(exerciceId)).hasSize(2); // jamais plus de deux (RG6 v2)
    }

    @Test
    void exerciceDeposeAvecDeuxPresentsRecoitDeuxRelecteursDistincts() throws Exception {
        JsonNode session = post("/api/sessions", "{\"titre\":\"Assignation 2\",\"promotionId\":1}");
        String code = session.get("code").asText();
        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":3}");
        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":5}");

        JsonNode depot = post("/api/exercices", "{\"sessionId\":" + session.get("id").asLong()
                + ",\"etudiantId\":4,\"lien\":\"https://github.com/daniel/exo\"}");

        assertThat(depot.get("statut").asText()).isEqualTo("EN_ATTENTE_RELECTURE");
        assertThat(relecteurs(depot.get("id").asLong())).containsExactlyInAnyOrder(3L, 5L);
    }
}
