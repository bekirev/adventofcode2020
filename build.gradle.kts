import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "me.abekirev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotestVersion = "4.3.1"
val mockitoVersion = "3.6.28"
val kotlinMockitoVersion = "2.2.0"

dependencies {
    implementation(project(":grid"))
    implementation(project(":util"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$kotlinMockitoVersion")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "14"
}

application {
    mainClassName = "MainKt"
}

subprojects {
    tasks.withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "14"
    }

    repositories {
        mavenCentral()
    }
}