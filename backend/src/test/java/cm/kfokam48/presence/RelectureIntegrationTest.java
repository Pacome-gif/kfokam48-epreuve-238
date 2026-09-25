package cm.kfokam48.presence;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** POST /api/relectures/{id} : codes 200, 400, 403, 409 du contrat. */
@SpringBootTest
@AutoConfigureMockMvc
class RelectureIntegrationTest {

    // Promotion B (données de démo) : Grace = 7 auteure, Hervé = 8 seul autre présent donc relecteur
    static final long AUTEURE = 7;
    static final long RELECTEUR = 8;
    static final long AUTRE = 9;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    long relectureId;

    JsonNode appel(String url, String corps) throws Exception {
        return json.readTree(mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(corps))
                .andReturn().getResponse().getContentAsString());
    }

    @BeforeEach
    void preparer() throws Exception {
        JsonNode session = appel("/api/sessions", "{\"titre\":\"Relecture\",\"promotionId\":2}");
        String code = session.get("code").asText();
        appel("/api/presences", "{\"code\":\"" + code + "\",\"etudiantId\":" + RELECTEUR + "}");
        appel("/api/exercices", "{\"sessionId\":" + session.get("id").asLong() + ",\"etudiantId\":" + AUTEURE
                + ",\"lien\":\"https://github.com/grace/exo\"}");
        JsonNode aFaire = json.readTree(mvc.perform(get("/api/etudiants/" + RELECTEUR + "/relectures"))
                .andReturn().getResponse().getContentAsString());
        relectureId = aFaire.get(0).get("id").asLong();
    }

    ResultActions rendre(long etudiantId, String note) throws Exception {
        return mvc.perform(post("/api/relectures/" + relectureId).header("X-Etudiant-Id", etudiantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"note\":" + note + ",\"commentaire\":\"Bien structuré\"}"));
    }

    @Test
    void relectureRenduePuisDefinitive() throws Exception {
        rendre(RELECTEUR, "16")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rendue").value(true))
                .andExpect(jsonPath("$.note").value(16));

        rendre(RELECTEUR, "18")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RELECTURE_DEJA_RENDUE"));
    }

    @Test
    void noteHorsBornesOuDecimaleRenvoie400() throws Exception {
        for (String note : new String[] { "21", "-1", "12.5" }) {
            rendre(RELECTEUR, note)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("NOTE_INVALIDE"));
        }
    }

    @Test
    void relireSonPropreExerciceRenvoie403() throws Exception {
        rendre(AUTEURE, "20")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTO_RELECTURE"));
    }

    @Test
    void unAutreEtudiantQueLeRelecteurRenvoie403() throws Exception {
        rendre(AUTRE, "10")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("NON_RELECTEUR"));
    }
}
