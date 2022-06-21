// Configuration inspired by https://github.com/erikc5000/island-time

plugins {
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

val NEXUS_USERNAME: String? by project
val NEXUS_PASSWORD: String? by project
val SIGNING_KEY: String? by project
val SIGNING_PWD: String? by project
val USE_SNAPSHOT: String? by project
val isRelease = USE_SNAPSHOT == null

if (!isRelease) {
    version = "$version-SNAPSHOT"
}

val javadocJar by tasks.registering(Jar::class) {
    val dokkaHtml = tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml")
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.get().outputDirectory)
}

if (SIGNING_KEY.isNullOrEmpty() || SIGNING_PWD.isNullOrEmpty()) {
    logger.warn("Signing Disable as the PGP key was not found")
} else {
    logger.warn("Using SIGNING_KEY and SIGNING_PWD")
    signing {
        useInMemoryPgpKeys(SIGNING_KEY, SIGNING_PWD)
        sign(publishing.publications)
    }
}

tasks.withType<Sign>().configureEach {
    onlyIf { isRelease }
}

publishing {
    repositories {
        maven {
            val snapshotRepositoryUrl: String by project
            val releaseRepositoryUrl: String by project
            val repositoryUrl = if (isRelease) releaseRepositoryUrl else snapshotRepositoryUrl

            url = uri(repositoryUrl)

            credentials {
                username = NEXUS_USERNAME.orEmpty()
                password = NEXUS_PASSWORD.orEmpty()
            }
        }
    }

    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar.get())

        val pomName: String by project
        val pomDescription: String by project
        val pomScmUrl: String? by project
        val pomUrl: String? by project
        val pomScmConnection: String? by project
        val pomLicenseName: String? by project
        val pomLicenseUrl: String? by project
        val pomLicenseDist: String? by project
        val pomDeveloperId: String? by project
        val pomDeveloperName: String? by project
        val pomArtifactId: String? by project

        if (pomArtifactId != null) {
            artifactId = pomArtifactId
        }

        pom {
            name.set(pomName)
            description.set(pomDescription)
            url.set(pomUrl)
            licenses {
                license {
                    name.set(pomLicenseName)
                    url.set(pomLicenseUrl)
                    distribution.set(pomLicenseDist)
                }
            }
            developers {
                developer {
                    id.set(pomDeveloperId)
                    name.set(pomDeveloperName)

                }
            }
            scm {
                connection.set(pomScmConnection)
                url.set(pomScmUrl)
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "13"
    }
}

tasks.withType(AbstractTestTask::class).configureEach {
    testLogging {
        events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStackTraces = true
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            val pomArtifactId: String? by project
            val pomMppArtifactId: String? by project
            (pomArtifactId ?: pomMppArtifactId)?.let { moduleName.set(it) }
            skipEmptyPackages.set(true)
            skipDeprecated.set(true)
        }
    }
}
