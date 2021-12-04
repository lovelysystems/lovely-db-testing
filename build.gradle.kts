import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.lovelysystems.gradle") version "1.6.1"
    `java-library`
    `maven-publish`
}

group = "com.lovelysystems"

lovely {
    gitProject()
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.junit.jupiter:junit-jupiter:5.8.1")
    api("org.testcontainers:testcontainers:1.16.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation(kotlin("test-junit5"))
}


tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events = setOf(PASSED, SKIPPED, FAILED)
    }
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
