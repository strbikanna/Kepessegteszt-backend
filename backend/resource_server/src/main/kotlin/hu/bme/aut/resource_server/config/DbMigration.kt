package hu.bme.aut.resource_server.config

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Flyway migration strategy.
 */
@Configuration
class DbMigration {
    @Bean
    fun flywayMigration(): FlywayMigrationStrategy? {
        return FlywayMigrationStrategy { flyway: Flyway ->
            //flyway.baseline()
            flyway.repair()
            flyway.migrate()
        }
    }
}