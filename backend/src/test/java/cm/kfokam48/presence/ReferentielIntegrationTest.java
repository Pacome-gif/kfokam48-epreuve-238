package cm.kfokam48.presence;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Le socle démarre sur H2 avec les migrations Flyway et les données de démo,
 * et une erreur renvoie toujours {code, message}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReferentielIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void listeLesPromotionsDeDemo() throws Exception {
        mvc.perform(get("/api/promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void promotionInconnueRenvoie404AuFormatDuContrat() throws Exception {
        mvc.perform(get("/api/promotions/9999/etudiants"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void routeInconnueRenvoieLeFormatDuContrat() throws Exception {
        mvc.perform(get("/api/nexiste-pas"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESSOURCE_INTROUVABLE"));
    }
}
