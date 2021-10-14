plugins {
    kotlin("jvm") version "1.5.31"
    id("com.lovelysystems.gradle") version "1.6.1"
}

group = "com.lovelysystems"

lovely {
    gitProject()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}