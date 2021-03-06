plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.5.0"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
    implementation("org.jetbrains.dokka:dokka-core:1.4.32")
}