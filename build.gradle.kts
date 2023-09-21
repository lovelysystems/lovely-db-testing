import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    kotlin("jvm") version "1.9.10"
    id("com.lovelysystems.gradle") version "1.12.0"
    `java-library`
    `maven-publish`
}


kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


group = "com.lovelysystems"

lovely {
    gitProject()
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.junit.jupiter:junit-jupiter:5.10.0")
    api("org.testcontainers:testcontainers:1.19.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation(kotlin("test-junit5"))
}


tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            // change to point to your repo, e.g. http://my.org/repo
            url = uri("../maven/releases")
        }
    }
}
