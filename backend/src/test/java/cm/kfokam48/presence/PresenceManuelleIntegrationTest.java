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

/** EF9 : présence ajoutée à la main par le formateur (Q14, RG15). */
@SpringBootTest
@AutoConfigureMockMvc
class PresenceManuelleIntegrationTest {

    // Séance de démo DEMO01 (id 1, promotion A) : son code a expiré depuis longtemps ; Franck (6) n'y était pas
    static final long SEANCE_DEMO = 1;
    static final long FRANCK = 6;

    @Autowired
    MockMvc mvc;

    ResultActions ajouter(long sessionId, String corps) throws Exception {
        return mvc.perform(post("/api/sessions/" + sessionId + "/presences")
                .contentType(MediaType.APPLICATION_JSON).content(corps));
    }

    @Test
    void presenceManuelleMemeCodeExpirePuisDoublonRefuse() throws Exception {
        ajouter(SEANCE_DEMO, "{\"etudiantId\":" + FRANCK + "}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.source").value("FORMATEUR"))
                .andExpect(jsonPath("$.etudiantId").value(FRANCK));

        ajouter(SEANCE_DEMO, "{\"etudiantId\":" + FRANCK + "}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEJA_PRESENT"));

        mvc.perform(get("/api/sessions/" + SEANCE_DEMO + "/presences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.etudiantId == 6)].source").value("FORMATEUR"))
                .andExpect(jsonPath("$[?(@.etudiantId == 6)].nom").value("Franck Essomba"));
    }

    @Test
    void etudiantDuneAutrePromotionRenvoie403() throws Exception {
        ajouter(SEANCE_DEMO, "{\"etudiantId\":7}")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("HORS_PROMOTION"));
    }

    @Test
    void seanceInconnueRenvoie404EtChampManquant400() throws Exception {
        ajouter(9999, "{\"etudiantId\":1}")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SESSION_INCONNUE"));
        ajouter(SEANCE_DEMO, "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"));
    }
}
