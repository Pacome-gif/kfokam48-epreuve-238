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

/**
 * Test d'intégration de POST /api/presences : codes HTTP du contrat et du diagramme D3.
 * Tourne sur H2 avec les migrations Flyway et les données de démo.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PresenceIntegrationTest {

    // Données de démo (V2) : promotion A = id 1, étudiants 1 à 6 ; promotion B = étudiants 7 à 10
    static final long PROMO_A = 1;
    static final long ESTELLE = 5;
    static final long GRACE_PROMO_B = 7;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    String ouvrirSeance() throws Exception {
        String corps = mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"titre\":\"Séance présence\",\"promotionId\":" + PROMO_A + "}"))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(corps).get("code").asText();
    }

    ResultActions marquer(String code, long etudiantId) throws Exception {
        return mvc.perform(post("/api/presences").contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"" + code + "\",\"etudiantId\":" + etudiantId + "}"));
    }

    @Test
    void casNominalPuisDejaPresent() throws Exception {
        String code = ouvrirSeance();

        marquer(code, ESTELLE)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.sessionId").isNumber())
                .andExpect(jsonPath("$.etudiantId").value(ESTELLE))
                .andExpect(jsonPath("$.source").value("ETUDIANT"));

        marquer(code, ESTELLE)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEJA_PRESENT"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void codeExpireRenvoie410() throws Exception {
        // La séance de démo DEMO01 a été ouverte le 18/09/2026 : son code a expiré
        marquer("DEMO01", 6)
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.code").value("CODE_EXPIRE"));
    }

    @Test
    void codeInconnuRenvoie400() throws Exception {
        marquer("ZZZZZZ", ESTELLE)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CODE_INCONNU"));
    }

    @Test
    void champManquantRenvoie400() throws Exception {
        mvc.perform(post("/api/presences").contentType(MediaType.APPLICATION_JSON).content("{\"code\":\"ABC\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"));
    }

    @Test
    void etudiantDuneAutrePromotionRenvoie403() throws Exception {
        marquer(ouvrirSeance(), GRACE_PROMO_B)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("HORS_PROMOTION"));
    }
}
