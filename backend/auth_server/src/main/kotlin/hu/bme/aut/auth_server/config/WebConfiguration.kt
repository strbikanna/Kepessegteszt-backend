package hu.bme.aut.auth_server.config;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class WebConfiguration {
    private final val CACHE_SIZE = 1000;

    @Bean
    fun userAgentAnalyzer() : UserAgentAnalyzer {
        return UserAgentAnalyzer
                .newBuilder()
                .withCache(CACHE_SIZE)
                .withField(UserAgent.DEVICE_CLASS)
                .build();
    }
}
