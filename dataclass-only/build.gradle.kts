plugins {
    `multiplatform-library`
}

kotlin {
    addKMTargets(logger = logger)
    addKMSources(logger = logger)
//    sourceSets {
//        val commonMain by getting
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
