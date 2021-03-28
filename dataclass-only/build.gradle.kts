plugins {
    kotlin("jvm") version "1.4.31"
}

group = "com.github.omarmiatello.telegram"
version = "5.1"

repositories {
    mavenCentral()
}

dependencies {
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}