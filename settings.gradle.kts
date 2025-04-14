pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "crawler"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("core")
