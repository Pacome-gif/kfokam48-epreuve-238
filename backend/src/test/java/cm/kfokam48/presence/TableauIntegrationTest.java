package cm.kfokam48.presence;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * GET /api/tableau sur les seules données de démo (base H2 dédiée, non modifiée par les autres tests).
 * V2 : Awa relue 15, Brice relu 12, Carine attend Daniel (1 relecteur requis, H11).
 * V4 : Daniel 14 provisoire (1/2 rendue), Estelle 13 et 16 → 14,5 définitive.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:tableau;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;"
        + "DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1")
class TableauIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void tableauDeLaPromotionA() throws Exception {
        mvc.perform(get("/api/tableau").param("promotionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                // tri par nom : Awa, Brice, Carine, Daniel, Estelle, Franck
                .andExpect(jsonPath("$[0].nom").value("Awa Mballa"))
                .andExpect(jsonPath("$[0].presences").value(2))
                .andExpect(jsonPath("$[0].exercicesDeposes").value(1))
                .andExpect(jsonPath("$[0].moyenne").value(15.0))
                .andExpect(jsonPath("$[0].moyenneProvisoire").value(false))
                .andExpect(jsonPath("$[0].relecturesEnAttente").value(0))
                .andExpect(jsonPath("$[1].moyenne").value(12.0))
                .andExpect(jsonPath("$[1].relecturesEnAttente").value(1))
                .andExpect(jsonPath("$[2].moyenne").value(nullValue()))
                .andExpect(jsonPath("$[2].exercicesEnAttente").value(1))
                .andExpect(jsonPath("$[3].moyenne").value(14.0))
                .andExpect(jsonPath("$[3].moyenneProvisoire").value(true))
                .andExpect(jsonPath("$[3].relecturesEnAttente").value(1))
                .andExpect(jsonPath("$[4].moyenne").value(14.5))
                .andExpect(jsonPath("$[4].moyenneProvisoire").value(false))
                .andExpect(jsonPath("$[4].presences").value(2))
                .andExpect(jsonPath("$[5].presences").value(0));
    }

    @Test
    void exercicesDeDanielNoteProvisoireSansIdentiteDesRelecteurs() throws Exception {
        mvc.perform(get("/api/etudiants/4/exercices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].note").value(14.0))
                .andExpect(jsonPath("$[0].noteProvisoire").value(true))
                .andExpect(jsonPath("$[0].relecturesRendues").value(1))
                .andExpect(jsonPath("$[0].relecturesAttendues").value(2))
                .andExpect(jsonPath("$[0].commentaires.length()").value(1))
                .andExpect(jsonPath("$[0].relecteurId").doesNotExist())
                .andExpect(jsonPath("$[0].relecteurs").doesNotExist());
    }

    @Test
    void etudiantInconnuRenvoie404() throws Exception {
        mvc.perform(get("/api/etudiants/999/exercices"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ETUDIANT_INCONNU"));
    }

    @Test
    void promotionInconnueRenvoie404() throws Exception {
        mvc.perform(get("/api/tableau").param("promotionId", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }

    @Test
    void promotionIdManquantRenvoie400() throws Exception {
        mvc.perform(get("/api/tableau"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"));
    }
}
