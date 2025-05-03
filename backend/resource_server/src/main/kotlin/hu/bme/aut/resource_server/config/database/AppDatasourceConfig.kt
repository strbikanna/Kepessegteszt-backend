package hu.bme.aut.resource_server.config.database

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "appEntityManagerFactory",
    transactionManagerRef = "appTransactionManager",
    basePackages = [
        "hu.bme.aut.resource_server.ability",
        "hu.bme.aut.resource_server.game",
        "hu.bme.aut.resource_server.profile",
        "hu.bme.aut.resource_server.profile_snapshot",
        "hu.bme.aut.resource_server.profile_calculation",
        "hu.bme.aut.resource_server.recommendation",
        "hu.bme.aut.resource_server.recommended_game",
        "hu.bme.aut.resource_server.result",
        "hu.bme.aut.resource_server.user",
        "hu.bme.aut.resource_server.user_group",
    ]
)
class AppDatasourceConfig(
    private val env: Environment
) {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.app-datasource")
    fun appDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    fun appDataSource(): DataSource {
        return appDataSourceProperties()
            .initializeDataSourceBuilder()
            .build()
    }

    @Bean
    @Primary
    fun appJdbcTemplate(@Qualifier("appDataSource") dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }

    @Bean
    @Primary
    fun appEntityManagerFactory(
        @Qualifier("appDataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.dataSource = dataSource
        entityManagerFactoryBean.setPackagesToScan(
            "hu.bme.aut.resource_server.ability",
            "hu.bme.aut.resource_server.game",
            "hu.bme.aut.resource_server.profile",
            "hu.bme.aut.resource_server.profile_snapshot",
            "hu.bme.aut.resource_server.profile_calculation",
            "hu.bme.aut.resource_server.recommendation",
            "hu.bme.aut.resource_server.recommended_game",
            "hu.bme.aut.resource_server.result",
            "hu.bme.aut.resource_server.user",
            "hu.bme.aut.resource_server.user_group"
        )
        entityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
        entityManagerFactoryBean.persistenceUnitName = "appPersistenceUnit"
        entityManagerFactoryBean.setJpaPropertyMap(
            mapOf(
                "hibernate.dialect" to env.getProperty("spring.jpa.properties.hibernate.dialect"),
            )
        )
        return entityManagerFactoryBean
    }

    @Bean
    @Primary
    fun appTransactionManager(
        @Qualifier("appEntityManagerFactory") appEntityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(
            Objects.requireNonNull(appEntityManagerFactory.getObject())!!
        )
    }


}