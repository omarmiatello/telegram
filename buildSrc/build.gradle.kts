plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.7.0"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.20")
    implementation("org.jetbrains.dokka:dokka-core:1.6.20")
}