package com.quebin31.project.jacoco

import com.quebin31.project.extensions.isExcludedFromCoverageReport
import org.gradle.api.reporting.DirectoryReport
import org.gradle.api.reporting.SingleFileReport
import org.gradle.kotlin.dsl.getByName
import org.gradle.testing.jacoco.tasks.JacocoReport

open class JacocoMergedReport : JacocoReport() {

    fun configureReports(vararg dependsOn: String) {
        val enabledSubprojects = project.subprojects.filterNot {
            it.isExcludedFromCoverageReport()
        }

        val enabledSubprojectTasks = enabledSubprojects
            .flatMap { project -> dependsOn.mapNotNull { project.tasks.findByName(it) } }

        dependsOn(enabledSubprojectTasks)

        reports {
            val reportsOutput = "${project.layout.buildDirectory}/reports/coverage/merged"

            getByName<DirectoryReport>("html") {
                required.set(true)
                outputLocation.set(project.file("$reportsOutput/html"))
            }

            getByName<SingleFileReport>("xml") {
                required.set(true)
                outputLocation.set(project.file("$reportsOutput/report.xml"))
            }
        }

        val mergedClassDirectories = enabledSubprojects
            .map {
                it.fileTree("${it.layout.buildDirectory}/tmp/kotlin-classes/debug").run {
                    exclude("**/R.class", "**/R$*.class")
                    exclude("**/BuildConfig", "**/BuildConfig.*", "**/Manifest*.*")
                    exclude("**/*Test*.*")
                    exclude("android/**/*.*")
                    exclude("**/components/**/*Params*")
                    exclude("**/*ComposableSingletons*")
                    exclude("**/*Preview*", "**/*PreviewProvider*")
                    asFileTree
                }
            }
            .reduce { merged, tree -> merged.plus(tree) }

        val mergedExecutionData = enabledSubprojects
            .map {
                it.fileTree("${it.layout.buildDirectory}/outputs").run {
                    include("unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
                    include("code_coverage/debugAndroidTest/**/coverage.ec")
                    asFileTree
                }
            }
            .reduce { merged, tree -> merged.plus(tree) }

        val mergedSourceDirectories = enabledSubprojects
            .map { "${it.projectDir}/src/main/kotlin" }

        classDirectories.setFrom(mergedClassDirectories)
        executionData.setFrom(mergedExecutionData)
        sourceDirectories.setFrom(mergedSourceDirectories)
    }
}
