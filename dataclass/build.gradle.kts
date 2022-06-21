plugins {
    `multiplatform-library`
    kotlin("plugin.serialization")
}

kotlin {
    addKMTargets(logger = logger)
    addKMSources(
        commonMain = {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
            }
        },
        logger = logger,
    )
//    sourceSets {
//        val commonMain by getting {
//            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
//            }
//        }
////        val commonTest by getting {
////            dependencies {
////                implementation(kotlin("test-common"))
////                implementation(kotlin("test-annotations-common"))
////            }
////        }
////        val jvmMain by getting
////        val jvmTest by getting {
////            dependencies {
////                implementation(kotlin("test-junit"))
////            }
////        }
////        val jsMain by getting
////        val jsTest by getting {
////            dependencies {
////                implementation(kotlin("test-js"))
////            }
////        }
////        val nativeMain by getting
////        val nativeTest by getting
//    }
}
