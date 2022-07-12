plugins {
    `multiplatform-library`
}

kotlin {
    addKMTargets(logger = logger)
    addKMSources(
        commonMain = {
            dependencies {
                api(project(":dataclass"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
                implementation("io.ktor:ktor-client-core:1.5.1")
            }
        },
        logger = logger,
    )
//    sourceSets {
//        val commonMain by getting {
//            dependencies {
//                api(project(":dataclass"))
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
//                implementation("io.ktor:ktor-client-core:1.5.1")
//            }
//        }
//        // val commonTest by getting {
//        //     dependencies {
//        //         implementation(kotlin("test-common"))
//        //         implementation(kotlin("test-annotations-common"))
//        //     }
//        // }
//        // val jvmMain by getting
//        // val jvmTest by getting {
//        //     dependencies {
//        //         implementation(kotlin("test-junit"))
//        //     }
//        // }
//        // val jsMain by getting
//        // val jsTest by getting {
//        //     dependencies {
//        //         implementation(kotlin("test-js"))
//        //     }
//        // }
//        // val nativeMain by getting
//        // val nativeTest by getting
//
//
//    }
}
