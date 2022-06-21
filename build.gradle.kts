plugins {
    kotlin("plugin.serialization") version "1.7.0" apply false
}

allprojects {
    group = "com.github.omarmiatello.telegram"

    repositories {
        mavenCentral()
    }
}
