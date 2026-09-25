package cm.kfokam48.presence;

import static org.assertj.core.api.Assertions.assertThat;

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

/** EF4 et le trou H1 : assignation au dépôt, ou à la prochaine présence s'il n'y a personne. */
@SpringBootTest
@AutoConfigureMockMvc
class AssignationIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    @Autowired
    ExerciceRepository exercices;

    JsonNode post(String url, String corps) throws Exception {
        return json.readTree(mvc.perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).content(corps))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    void exerciceSansAutrePresentAttendPuisEstAssigneALaPresenceSuivante() throws Exception {
        JsonNode session = post("/api/sessions", "{\"titre\":\"Assignation\",\"promotionId\":1}");
        String code = session.get("code").asText();
        long sessionId = session.get("id").asLong();

        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":1}");
        JsonNode depot = post("/api/exercices",
                "{\"sessionId\":" + sessionId + ",\"etudiantId\":1,\"lien\":\"https://github.com/awa/exo\"}");
        assertThat(depot.get("statut").asText()).isEqualTo("DEPOSE");

        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":2}");

        assertThat(exercices.findById(depot.get("id").asLong()).orElseThrow().getStatut())
                .isEqualTo(StatutExercice.EN_ATTENTE_RELECTURE);
    }

    @Test
    void exerciceDeposeAvecDesPresentsEstAssigneImmediatement() throws Exception {
        JsonNode session = post("/api/sessions", "{\"titre\":\"Assignation 2\",\"promotionId\":1}");
        String code = session.get("code").asText();
        post("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":3}");

        JsonNode depot = post("/api/exercices", "{\"sessionId\":" + session.get("id").asLong()
                + ",\"etudiantId\":4,\"lien\":\"https://github.com/daniel/exo\"}");

        assertThat(depot.get("statut").asText()).isEqualTo("EN_ATTENTE_RELECTURE");
    }
}
