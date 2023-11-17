package com.speechify.multiplatformswiftpackage

import com.speechify.multiplatformswiftpackage.domain.AppleTarget
import com.speechify.multiplatformswiftpackage.domain.platforms
import com.speechify.multiplatformswiftpackage.task.registerCreateSwiftPackageTask
import com.speechify.multiplatformswiftpackage.task.registerCreateXCFrameworkTask
import com.speechify.multiplatformswiftpackage.task.registerCreateZipFileTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Plugin to generate XCFramework and Package.swift file for Apple platform targets.
 */
class MultiplatformSwiftPackagePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<SwiftPackageExtension>(EXTENSION_NAME, project)

        project.afterEvaluate {
            project.extensions.findByType<KotlinMultiplatformExtension>()?.let { kmpExtension ->
                extension.appleTargets = AppleTarget.allOf(
                    nativeTargets = kmpExtension.targets.toList(),
                    platforms = extension.targetPlatforms.platforms
                )
                project.registerCreateXCFrameworkTask()
                project.registerCreateSwiftPackageTask()
            }
        }
    }

    internal companion object {
        internal const val EXTENSION_NAME = "multiplatformSwiftPackage"
    }
}
