import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.springframework.boot)
	alias(libs.plugins.spring.dependency.management)
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.spring)
	alias(libs.plugins.kotlin.jpa)
	alias(libs.plugins.flyway)
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
}

dependencies {
	implementation(libs.spring.boot.starter.jpa)
	implementation(libs.spring.boot.starter.oauth2.authorization)
	implementation(libs.spring.boot.starter.security)
	implementation(libs.spring.boot.starter.web)
	implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.jackson.kotlin)
	implementation(libs.kotlin.reflect)
	implementation (libs.flyway.core)
	implementation (libs.flyway.mysql)
	implementation(libs.openapi.starter.webmvc)
	runtimeOnly(libs.mysql.connector)
	runtimeOnly(libs.h2)
	annotationProcessor(libs.lombok)
	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.spring.security.test)
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
