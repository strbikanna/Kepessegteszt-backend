package hu.bme.aut.resource_server.config.database

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class AiDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.ai-datasource")
    fun todosDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    fun aiDataSource(): DataSource {
        return todosDataSourceProperties()
            .initializeDataSourceBuilder()
            .build()
    }
}