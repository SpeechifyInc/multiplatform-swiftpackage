package com.speechify.multiplatformswiftpackage.domain

import com.speechify.multiplatformswiftpackage.MultiplatformSwiftPackagePlugin
import com.speechify.multiplatformswiftpackage.SwiftPackageExtension
import com.speechify.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

@Throws(InvalidUserDataException::class)
internal fun Project.getConfigurationOrThrow() = PluginConfiguration.of(
    extensions.findByName(MultiplatformSwiftPackagePlugin.EXTENSION_NAME) as SwiftPackageExtension
).fold({
    throw InvalidUserDataException(
        "${it.toErrorMessage()}\nFind more information on https://github.com/ge-org/multiplatform-swiftpackage"
    )
}, { it })

private fun List<PluginConfigurationError>.toErrorMessage() = joinToString("\n\n") { error ->
    when (error) {
        PluginConfigurationError.MissingSwiftToolsVersion -> """
        * Swift tools version is missing.
          Declare it by adding your Swift version to the plugin configuration block.
        """.trimIndent()
        PluginConfigurationError.MissingTargetPlatforms -> """
        * Target platforms are missing.
          Declare at least one platform by adding it to the plugin configuration block.
        """.trimIndent()
        PluginConfigurationError.MissingAppleTargets -> """
        * No Apple targets declared.
          It appears your multiplatform project does not contain any Apple target. 
        """.trimIndent()
        is PluginConfigurationError.InvalidTargetName -> """
        * Target name is invalid: ${error.name}
          Only the following target names are valid: ${TargetName.values().joinToString { it.identifier }}
        """.trimIndent()
        PluginConfigurationError.BlankPackageName -> """
        * Package name must not be blank
          Either declare the base name of your frameworks or use a non-empty package name.
        """.trimIndent()
        PluginConfigurationError.BlankZipFileName -> """
        * ZIP file name must not be blank
        """.trimIndent()
    }
}
