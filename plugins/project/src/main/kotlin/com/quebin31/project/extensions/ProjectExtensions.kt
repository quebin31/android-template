package com.quebin31.project.extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.util.Properties

fun Project.getLocalProperty(name: String): String? = try {
    val localPropsFile = rootProject.file("local.properties")
    val properties = Properties().apply { load(localPropsFile.inputStream()) }
    properties.getProperty(name)
} catch (e: Throwable) {
    null
}

private object Extras {
    const val ExcludeFromApiValidation = "com.quebin31.project.ignoreFromApiValidation"
    const val ExcludeFromCoverageReport = "com.quebin31.project.excludeFromCoverageReport"
}

fun Project.excludeFromApiValidation() {
    project.extra.set(Extras.ExcludeFromApiValidation, true)
}

fun Project.isExcludedFromApiValidation(): Boolean = try {
    project.extra.get(Extras.ExcludeFromApiValidation) as? Boolean ?: false
} catch (e: Throwable) {
    false
}

fun Project.excludeFromCoverageReport() {
    project.extra.set(Extras.ExcludeFromCoverageReport, true)
}

fun Project.isExcludedFromCoverageReport(): Boolean = try {
    project.extra.get(Extras.ExcludeFromCoverageReport) as? Boolean ?: false
} catch (e: Throwable) {
    false
}
