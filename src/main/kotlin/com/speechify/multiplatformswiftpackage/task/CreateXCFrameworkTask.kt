package com.speechify.multiplatformswiftpackage.task

import com.speechify.multiplatformswiftpackage.domain.AppleFramework
import com.speechify.multiplatformswiftpackage.domain.fromFatBinary
import com.speechify.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

internal fun Project.registerCreateXCFrameworkTask() =
    tasks.register("createXCFramework", Exec::class.java) {
        group = "multiplatform-swift-package"
        description = "Creates an XCFramework for all declared Apple targets"

        val configuration = getConfigurationOrThrow()
        val xcFrameworkDestination = File(
            configuration.outputDirectory.value,
            "${configuration.packageName.value}.xcframework"
        )
        val frameworks =
            configuration.appleTargets.mapNotNull { it.getFramework(configuration.buildConfiguration) }.toMutableList()

        dependsOn(frameworks.map { it.linkTask.name })

        doFirst {
            xcFrameworkDestination.deleteRecursively()
            val iosSimulatorArm64Binary = frameworks.first { it.outputFile.path.contains("iosSimulatorArm64") }
            val iosX64Binary = frameworks.first { it.outputFile.path.contains("iosX64") }
            val fatBinaryDir = buildDir.resolve("bin/iosSimulatorArm64X64/podReleaseFramework/${iosSimulatorArm64Binary.outputFile.file.name}").apply { mkdirs() }
            fatBinaryDir.parentFile.parentFile.deleteRecursively()
            val binaryBaseName = iosSimulatorArm64Binary.outputFile.file.name.substringBefore(".framework")
            fatBinaryDir.deleteRecursively()
            iosSimulatorArm64Binary.outputFile.file.parentFile.toPath().copyTo(fatBinaryDir.parentFile.toPath())
            project.exec {
                executable = "lipo"
                args = listOf("-create", "${iosSimulatorArm64Binary.outputFile.path}/${iosSimulatorArm64Binary.name.value}", "${iosX64Binary.outputFile.path}/${iosX64Binary.name.value}", "-output", fatBinaryDir.resolve(binaryBaseName).path)
            }
            val fatFramework = AppleFramework.fromFatBinary(
                    fatBinaryFile = fatBinaryDir,
                    baseName = iosSimulatorArm64Binary.name.value,
                    linkTaskName = "placeholder"
            )

            iosSimulatorArm64Binary.outputFile.parent.parentFile.deleteRecursively()
            iosX64Binary.outputFile.parent.parentFile.deleteRecursively()
            frameworks.removeIf { it.outputFile.path.contains("iosSimulatorArm64") }
            frameworks.removeIf { it.outputFile.path.contains("iosX64") }
            frameworks.add(fatFramework)

            executable = "xcodebuild"
            args(mutableListOf<String>().apply {
                add("-create-xcframework")
                add("-output")
                add(xcFrameworkDestination.path)
                frameworks.forEach { framework ->
                    add("-framework")
                    add(framework.outputFile.path)

                    framework.dsymFile.takeIf { it.exists() }?.let { dsymFile ->
                        add("-debug-symbols")
                        add(dsymFile.path)
                    }
                }
            })
        }
    }

fun Path.copyTo(target: Path, overwrite: Boolean = false) {
    Files.walk(this).use { stream ->
        stream.forEach { sourcePath ->
            target.resolve(this.relativize(sourcePath)).also { targetPath ->
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath)
                } else {
                    Files.copy(sourcePath, targetPath, if (overwrite) StandardCopyOption.REPLACE_EXISTING else StandardCopyOption.COPY_ATTRIBUTES)
                }
            }
        }
    }
}