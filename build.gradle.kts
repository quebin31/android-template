import com.quebin31.project.extensions.pluginId
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

// Top-level build file where you can add configuration options common to all sub-projects/modules.

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    jacoco
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.android.lib) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.versions)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.idea)
    alias(libs.plugins.project)
}

idea.project.settings {
    taskTriggers {
        afterSync(tasks.named("addKtlintCheckGitPreCommitHook"))
    }
}

allprojects {
    apply(plugin = rootProject.libs.plugins.ktlint.pluginId)

    ktlint {
        version.set("0.45.2")
        android.set(true)
        debug.set(false)
        ignoreFailures.set(false)
        coloredOutput.set(false)

        reporters {
            reporter(ReporterType.PLAIN_GROUP_BY_FILE)
            reporter(ReporterType.HTML)
        }

        filter {
            exclude("**/generated/**")
        }
    }
}

subprojects {
    apply(plugin = "jacoco")

    tasks.configureEach {
        // Uninstall instrumentation tests APKs after run
        if (name == "connectedDebugAndroidTest") {
            finalizedBy("uninstallDebugAndroidTest")
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
