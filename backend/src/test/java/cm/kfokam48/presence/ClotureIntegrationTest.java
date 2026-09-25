package cm.kfokam48.presence;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** EF10 : clôture d'une séance et ses effets (RG3, RG10, H6). */
@SpringBootTest
@AutoConfigureMockMvc
class ClotureIntegrationTest {

    // Promotion B : Joël (10) dépose, Inès (9) est présente donc relectrice
    static final long AUTEUR = 10, RELECTRICE = 9, RETARDATAIRE = 8;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    ResultActions envoyer(String url, String corps) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(corps));
    }

    JsonNode corps(ResultActions r) throws Exception {
        return json.readTree(r.andReturn().getResponse().getContentAsString());
    }

    @Test
    void clotureBloquePresencesEtDepotsMaisPasLesRelectures() throws Exception {
        JsonNode seance = corps(envoyer("/api/sessions", "{\"titre\":\"Clôture\",\"promotionId\":2}"));
        long id = seance.get("id").asLong();
        String code = seance.get("code").asText();
        envoyer("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":" + RELECTRICE + "}");
        long exercice = corps(envoyer("/api/exercices", "{\"sessionId\":" + id + ",\"etudiantId\":" + AUTEUR
                + ",\"lien\":\"https://github.com/joel/exo\"}")).get("id").asLong();

        envoyer("/api/sessions/" + id + "/cloture", "")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clotureAt").isNotEmpty());

        envoyer("/api/sessions/" + id + "/cloture", "")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SESSION_DEJA_CLOTUREE"));

        envoyer("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":" + RETARDATAIRE + "}")
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.code").value("SESSION_CLOTUREE"));

        envoyer("/api/exercices", "{\"sessionId\":" + id + ",\"etudiantId\":" + RETARDATAIRE
                + ",\"lien\":\"https://github.com/herve/exo\"}")
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.code").value("SESSION_CLOTUREE"));

        long relecture = -1;
        for (JsonNode r : json.readTree(mvc.perform(get("/api/etudiants/" + RELECTRICE + "/relectures"))
                .andReturn().getResponse().getContentAsString())) {
            if (r.get("exerciceId").asLong() == exercice) {
                relecture = r.get("id").asLong();
            }
        }
        mvc.perform(post("/api/relectures/" + relecture).header("X-Etudiant-Id", RELECTRICE)
                .contentType(MediaType.APPLICATION_JSON).content("{\"note\":11,\"commentaire\":\"Rendu tardif\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void seanceInconnueRenvoie404() throws Exception {
        envoyer("/api/sessions/9999/cloture", "")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SESSION_INCONNUE"));
    }
}
