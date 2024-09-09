import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}
dependencyManagement {
    imports {
        mavenBom(libs.spring.azure.cloud.get().toString())
    }
}
group = "hu.bme.aut"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.spring.boot.starter.jpa)
    implementation(libs.spring.azure.starter.mysql)
    implementation(libs.spring.boot.starter.oauth2.resource)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.jackson.kotlin)
    //kotlin
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.jackson.jakarta.xmlbind.annotations)
    implementation(libs.hypersistence.hibernate62)
    implementation(libs.json)
    //jep
    implementation(libs.ninia.jep)
    //h2
    runtimeOnly(libs.h2)
    implementation(libs.spring.boot.starter.webflux)
    //flyway
    implementation (libs.flyway.core)
    implementation (libs.flyway.mysql)
    //swagger
    implementation(libs.openapi.starter.webmvc)
    //csv parser
    implementation(libs.csv)
    annotationProcessor(libs.spring.boot.configuration.processor)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.rest.assured)
    testImplementation(libs.hamcrest)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
