plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":client"))
    implementation(project(":dataclass"))
}
