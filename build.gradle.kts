plugins {
    kotlin("plugin.serialization") version "1.6.10" apply false
}

allprojects {
    group = "com.github.omarmiatello.telegram"

    repositories {
        mavenCentral()
    }
}
