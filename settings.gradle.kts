@file:Suppress("UnstableApiUsage")
pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            google()
            gradlePluginPortal()
            mavenCentral()
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.5.0")
}

rootProject.name = "multiplatform-swiftpackage"
