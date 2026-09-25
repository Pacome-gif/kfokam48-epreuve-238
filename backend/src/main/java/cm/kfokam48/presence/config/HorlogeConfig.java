package cm.kfokam48.presence.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Horloge injectable : les règles liées au temps (RG1, RG4) sont testables sans attendre.
 */
@Configuration
public class HorlogeConfig {

    @Bean
    public Clock horloge() {
        return Clock.systemUTC();
    }
}
