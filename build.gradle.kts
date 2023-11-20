@Suppress("DSL_SCOPE_VIOLATION")

repositories {
    google()
    mavenCentral()
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    libs.plugins.binary.compatibility.validator
}

version = Plugin.Config.version

dependencies {
    compileOnly(kotlin("gradle-plugin"))
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotest.property)
    testImplementation(libs.mockk)
    testImplementation(kotlin("gradle-plugin"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

gradlePlugin {
    plugins.create(Plugin.Config.name) {
        id = Plugin.Config.id
        implementationClass = Plugin.Config.implementationClass
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
                groupId = "com.speechify"
                artifactId = "multiplatform-swiftpackage.gradle.plugin"
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/speechifyinc/multiplatform-swiftpackage")

            credentials {
                // TODO: find ways to manage this better
                username = System.getenv("PLATFORM_USERNAME")
                password = System.getenv("PLATFORM_PAT")
            }
        }
    }

}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}