plugins {
    kotlin("multiplatform")
    id("published-library")
    //jacoco
}

kotlin {
    // explicitApi()
    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
            }
        }
    }

    targets.all {
        compilations.all {
            kotlinOptions.allWarningsAsErrors = true
        }
    }
}

publishing {
    val pomMppArtifactId: String? by project

    publications.withType<MavenPublication>().configureEach {
        if (pomMppArtifactId != null) {
            artifactId = if (name == "kotlinMultiplatform") pomMppArtifactId else "${pomMppArtifactId}-$name"
        }
    }
}

//afterEvaluate {
//    tasks.withType<JacocoReport>().configureEach {
//        classDirectories.setFrom(
//            fileTree("${buildDir}/classes/kotlin/jvm/") {
//                exclude("**/*Test*.*")
//            }
//        )
//
//        sourceDirectories.setFrom(kotlin.sourceSets["commonMain"].kotlin.sourceDirectories)
//        executionData.setFrom("${buildDir}/jacoco/jvmTest.exec")
//    }
//}

