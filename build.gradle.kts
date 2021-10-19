import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.11"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
}

group = "com.example.reactiveKotlin"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:2.5.5")
	implementation("org.springframework.boot:spring-boot-starter-validation:2.5.5")
	implementation("org.springframework.boot:spring-boot-starter-webflux:2.5.5")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.4")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2-native-mt")
	runtimeOnly("com.h2database:h2:1.4.200")
	runtimeOnly("io.r2dbc:r2dbc-h2:0.8.4.RELEASE")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.5")
	testImplementation("io.projectreactor:reactor-test:3.4.10")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
