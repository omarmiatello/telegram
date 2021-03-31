import org.jetbrains.kotlin.konan.properties.loadProperties

// Project configuration

val projectName = "telegram"
val projectVersion = "5.1"
val projectGroup = "com.github.omarmiatello.telegram"
val projectRepoPath = "github.com/omarmiatello/telegram"
val sharedArtifacts = mapOf(
    ":dataclass" to "Telegram Bot API in Kotlin: data class with Kotlinx/Serialization",
    ":client" to "Telegram Bot API in Kotlin: data class with Kotlinx/Serialization + Ktor client with method for Telegram bot API",
    ":dataclass-only" to "Telegram Bot API in Kotlin: data class only (serializer not included)"
)

// Plugins configuration

plugins {
    kotlin("jvm") version "1.4.31" apply false
    kotlin("plugin.serialization") version "1.4.30" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}


// mavenCentral() configuration

group = projectGroup
version = projectVersion

loadProperties(file("local.properties").absolutePath).also { p ->
    p.stringPropertyNames().forEach { key ->
        ext[key] = p.getProperty(key)
    }
}
val ossrhUsername: String? by ext
val ossrhPassword: String? by ext

nexusPublishing {
    repositories {
        create("myNexus") {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        }
    }
}

configure(subprojects.filter { it.path in sharedArtifacts.keys }) {
    apply<JavaLibraryPlugin>()
    apply<SigningPlugin>()
    apply<MavenPublishPlugin>()

    configure<JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()
    }

    configure<SigningExtension> {
        val publishing: PublishingExtension by project
        sign(publishing.publications)
    }

    configure<PublishingExtension> {
        publications {
            val main by creating(MavenPublication::class) {
                from(components["java"])

                pom {
                    name.set("$projectName-${project.name}")
                    description.set(sharedArtifacts.getValue(project.path))
                    url.set("https://$projectRepoPath")
                    group = projectGroup
                    version = projectVersion
                    licenses {
                        license {
                            name.set("MIT License")
                            // https://opensource.org/licenses/MIT
                            url.set("https://$projectRepoPath/blob/master/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("jackl85")
                            name.set("Omar Miatello")
                            email.set("omar.miatello@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:$projectRepoPath.git")
                        developerConnection.set("scm:git:ssh://$projectRepoPath.git")
                        url.set("https://$projectRepoPath/tree/master")
                    }
                }
            }
        }
        repositories {
            maven {
                name = project.name
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }
}
