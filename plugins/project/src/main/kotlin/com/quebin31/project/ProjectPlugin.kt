package com.quebin31.project

import com.quebin31.project.jacoco.JacocoMergedReport
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

abstract class ProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (project == project.rootProject) {
            project.rootProject.registerJacocoMergedReportTasks()
        }
    }

    private fun Project.registerJacocoMergedReportTasks() {
        tasks.register<JacocoMergedReport>("jacocoMergedReport") {
            configureReports()
        }

        tasks.register<JacocoMergedReport>("unitTestsCoverageReport") {
            configureReports("testDebugUnitTest")
        }

        tasks.register<JacocoMergedReport>("androidTestsCoverageReport") {
            configureReports("connectedDebugAndroidTest")
        }

        tasks.register<JacocoMergedReport>("allTestsCoverageReport") {
            configureReports("testDebugUnitTest", "connectedDebugAndroidTest")
        }
    }
}