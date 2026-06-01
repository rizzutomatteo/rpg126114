plugins {
    // Permette il download automatico del JDK richiesto dalla toolchain
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "pratiche-infernali"
include("app")
