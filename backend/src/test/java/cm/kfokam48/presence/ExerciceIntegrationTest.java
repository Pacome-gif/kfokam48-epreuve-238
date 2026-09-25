package cm.kfokam48.presence;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ExerciceIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    long ouvrirSeance() throws Exception {
        String corps = mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"titre\":\"Séance exercices\",\"promotionId\":1}"))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(corps).get("id").asLong();
    }

    ResultActions deposer(long sessionId, long etudiantId, String lien) throws Exception {
        return mvc.perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON)
                .content("{\"sessionId\":" + sessionId + ",\"etudiantId\":" + etudiantId + ",\"lien\":\"" + lien
                        + "\"}"));
    }

    @Test
    void depotPuisSecondDepotRefuse() throws Exception {
        long session = ouvrirSeance();

        deposer(session, 6, "https://github.com/franck/exo")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.statut").isString());

        deposer(session, 6, "https://github.com/franck/exo-v2")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EXERCICE_DEJA_DEPOSE"));
    }

    @Test
    void lienInvalideRenvoie400() throws Exception {
        long session = ouvrirSeance();
        for (String lien : new String[] { "pas-une-url", "ftp://serveur/exo", "javascript:alert(1)" }) {
            deposer(session, 4, lien)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("LIEN_INVALIDE"));
        }
    }

    @Test
    void seanceInconnueRenvoie404() throws Exception {
        deposer(9999, 4, "https://github.com/x")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SESSION_INCONNUE"));
    }
}
