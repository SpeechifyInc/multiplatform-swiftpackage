package com.speechify.multiplatformswiftpackage.task

import com.speechify.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip

internal fun Project.registerCreateZipFileTask() {
    tasks.register("createZipFile", Zip::class.java) {
        group = "" // hide the task from the task list
        description = "Creates a ZIP file containing the XCFramework"

        dependsOn("createXCFramework")

        val configuration = getConfigurationOrThrow()
        val outputDirectory = configuration.outputDirectory.value
        archiveFileName.set(configuration.zipFileName.nameWithExtension)
        destinationDirectory.set(outputDirectory)
        from(outputDirectory) {
            include("**/*.xcframework/")
        }
    }
}
