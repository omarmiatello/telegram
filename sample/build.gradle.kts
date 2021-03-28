plugins {
    kotlin("jvm") version "1.4.31"
}

version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":client"))
    implementation(project(":dataclass"))
}
