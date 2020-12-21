import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    idea
    kotlin("jvm") version "1.3.72"
}

group = "org.komamitsu"
version = "1.0-SNAPSHOT"

application {
  mainClassName = "org.komamitsu.konessem.MainKt"
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("no.tornado:tornadofx:1.7.19")
    implementation("org.slf4j:slf4j-simple:1.7.29")
    implementation("io.github.microutils:kotlin-logging:1.12.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
    testImplementation("io.mockk:mockk:1.10.2")
}

