import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

val currentOs = org.gradle.internal.os.OperatingSystem.current()!!
val isLinux = currentOs.isLinux
val isMacOsX = currentOs.isMacOsX
val isWindows = currentOs.isWindows
val isMainHost = isLinux

fun KotlinMultiplatformExtension.addKMTargets(logger: Logger) {
    if (true !in listOf(isLinux, isMacOsX, isWindows)) error("UNSUPPORTED host: ${currentOs.name}")

    targets {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "13"
            }
            testRuns["test"].executionTask.configure {
                useJUnit()
            }
        }

        if (isMainHost) {
            js(BOTH) {
                browser {
                    commonWebpackConfig {
                        cssSupport.enabled = true
                    }
                }
                nodejs()
                compilations.all {
                    kotlinOptions {
                        moduleKind = "umd"
                        sourceMap = true
                        sourceMapEmbedSources = "always"
                        metaInfo = true
                    }
                }
            }
            // wasm32()
        }

        if (isLinux) {
            // androidNativeArm32()
            // androidNativeArm64()
            // linuxArm32Hfp()
            // linuxArm64()
            // linuxMips32()
            // linuxMipsel32()
            linuxX64()
        }

        if (isMacOsX) {
            // iosArm32()
            iosArm64()
            iosX64()
            macosX64()
            tvosArm64()
            tvosX64()
            // watchosArm32()
            watchosArm64()
            watchosX86()
        }

        if (isWindows) {
            mingwX64 {
                // binaries.findTest(DEBUG)!!.linkerOpts = mutableListOf("-Wl,--subsystem,windows")
            }
            // mingwX86()
        }
    }

    logger.lifecycle("Targets [${targets.size}]: ${targets.map { it.name }.sorted().joinToString()}")
}

fun KotlinMultiplatformExtension.addKMSources(
    commonMain: KotlinSourceSet.() -> Unit = {},
    commonTest: KotlinSourceSet.() -> Unit = {},
    jvmMain: KotlinSourceSet.() -> Unit = {},
    jvmTest: KotlinSourceSet.() -> Unit = {},
    jsMain: KotlinSourceSet.() -> Unit = {},
    linuxX64Main: KotlinSourceSet.() -> Unit = {},
    macosX64Main: KotlinSourceSet.() -> Unit = {},
    macosX64Test: KotlinSourceSet.() -> Unit = {},
    iosArm64Main: KotlinSourceSet.() -> Unit = {},
    iosArm64Test: KotlinSourceSet.() -> Unit = {},
    iosX64Main: KotlinSourceSet.() -> Unit = {},
    iosX64Test: KotlinSourceSet.() -> Unit = {},
    watchosArm64Main: KotlinSourceSet.() -> Unit = {},
    watchosArm64Test: KotlinSourceSet.() -> Unit = {},
    watchosX86Main: KotlinSourceSet.() -> Unit = {},
    watchosX86Test: KotlinSourceSet.() -> Unit = {},
    tvosArm64Main: KotlinSourceSet.() -> Unit = {},
    tvosArm64Test: KotlinSourceSet.() -> Unit = {},
    tvosX64Main: KotlinSourceSet.() -> Unit = {},
    tvosX64Test: KotlinSourceSet.() -> Unit = {},
    mingwX64Main: KotlinSourceSet.() -> Unit = {},
    mingwX64Test: KotlinSourceSet.() -> Unit = {},
    logger: Logger,
) {
    sourceSets {
        if (commonMain != null) {
            val commonMain by getting(commonMain)
        }
        if (commonTest != null) {
            val commonTest by getting(commonTest)
        }

        if (jvmMain != null && targets.findByName("jvm") != null) {
            val jvmMain by getting(jvmMain)
        }
        if (jvmTest != null && targets.findByName("jvm") != null) {
            val jvmTest by getting(jvmTest)
        }
        if (jsMain != null && targets.findByName("jsLegacy") != null) {
            val jsMain by getting(jsMain)
        }

        if (linuxX64Main != null && targets.findByName("linuxX64") != null) {
            val linuxX64Main by getting(linuxX64Main)
        }
        // if (linuxArm32HfpMain != null && targets.findByName("linuxArm32Hfp") != null) {
        //     val linuxArm32HfpMain by getting(linuxArm32HfpMain)
        // }

        if (macosX64Main != null && targets.findByName("macosX64") != null) {
            val macosX64Main by getting(macosX64Main)
        }
        if (macosX64Test != null && targets.findByName("macosX64") != null) {
            val macosX64Test by getting(macosX64Test)
        }
        if (iosArm64Main != null && targets.findByName("iosArm64") != null) {
            val iosArm64Main by getting(iosArm64Main)
        }
        if (iosArm64Test != null && targets.findByName("iosArm64") != null) {
            val iosArm64Test by getting(iosArm64Test)
        }
        // if (iosArm32Main != null && targets.findByName("iosArm32") != null) {
        //     val iosArm32Main by getting(iosArm32Main)
        // }
        // if (iosArm32Test != null && targets.findByName("iosArm32") != null) {
        //     val iosArm32Test by getting(iosArm32Test)
        // }
        if (iosX64Main != null && targets.findByName("iosX64") != null) {
            val iosX64Main by getting(iosX64Main)
        }
        if (iosX64Test != null && targets.findByName("iosX64") != null) {
            val iosX64Test by getting(iosX64Test)
        }
        // if (watchosArm32Main != null && targets.findByName("watchosArm32") != null) {
        //     val watchosArm32Main by getting(watchosArm32Main)
        // }
        // if (watchosArm32Test != null && targets.findByName("watchosArm32") != null) {
        //     val watchosArm32Test by getting(watchosArm32Test)
        // }
        if (watchosArm64Main != null && targets.findByName("watchosArm64") != null) {
            val watchosArm64Main by getting(watchosArm64Main)
        }
        if (watchosArm64Test != null && targets.findByName("watchosArm64") != null) {
            val watchosArm64Test by getting(watchosArm64Test)
        }
        if (watchosX86Main != null && targets.findByName("watchosX86") != null) {
            val watchosX86Main by getting(watchosX86Main)
        }
        if (watchosX86Test != null && targets.findByName("watchosX86") != null) {
            val watchosX86Test by getting(watchosX86Test)
        }
        if (tvosArm64Main != null && targets.findByName("tvosArm64") != null) {
            val tvosArm64Main by getting(tvosArm64Main)
        }
        if (tvosArm64Test != null && targets.findByName("tvosArm64") != null) {
            val tvosArm64Test by getting(tvosArm64Test)
        }
        if (tvosX64Main != null && targets.findByName("tvosX64") != null) {
            val tvosX64Main by getting(tvosX64Main)
        }
        if (tvosX64Test != null && targets.findByName("tvosX64") != null) {
            val tvosX64Test by getting(tvosX64Test)
        }
        if (mingwX64Main != null && targets.findByName("mingwX64") != null) {
            val mingwX64Main by getting(mingwX64Main)
        }
        if (mingwX64Test != null && targets.findByName("mingwX64") != null) {
            val mingwX64Test by getting(mingwX64Test)
        }

        logger.lifecycle("SourceSets [${names.size}]: ${names.sorted().joinToString()}")
    }
}

/**
 * Configures the `targets` extension.
 *
 * `targets` is not accessible in a type safe way because:
 * - `org.gradle.api.NamedDomainObjectCollection` parameter types are missing
 */
private fun KotlinMultiplatformExtension.targets(configure: Action<Any>): Unit =
    (this as ExtensionAware).extensions.configure("targets", configure)

/**
 * Configures the [sourceSets][org.gradle.api.NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>] extension.
 */
private fun KotlinMultiplatformExtension.sourceSets(
    configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>
): Unit = (this as ExtensionAware).extensions.configure("sourceSets", configure)
