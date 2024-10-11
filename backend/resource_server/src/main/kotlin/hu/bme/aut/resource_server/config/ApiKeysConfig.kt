package hu.bme.aut.resource_server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "api-keys")
class ApiKeysConfig {
    lateinit var mistral: ApiConfig
    lateinit var openai: ApiConfig
    lateinit var gemini: ApiConfig

    class ApiConfig {
        lateinit var apiKey: String
    }
}