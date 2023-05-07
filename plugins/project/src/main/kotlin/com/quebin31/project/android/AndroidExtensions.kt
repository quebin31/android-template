@file:Suppress("UnstableApiUsage")

package com.quebin31.project.android

import com.android.build.api.dsl.VariantDimension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.quebin31.project.extensions.quoted
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

private const val CompileSdkVersion = 33
private const val MinSdkVersion = 24
private const val TargetSdkVersion = 33
private const val GroupId = "com.quebin31.project.template"
private const val VersionCode = 1
private const val VersionName = "0.1.0"

/**
 * Returns `true` if the current project is configured as an Android library, otherwise false.
 */
fun Project.isAndroidLibrary(): Boolean = try {
    project.extensions.getByType<BaseExtension>() is LibraryExtension
} catch (e: Throwable) {
    false
}

/**
 * Returns the Android namespace, if any.
 */
fun Project.androidNamespace(): String? = try {
    project.extensions.getByType<BaseExtension>().namespace
} catch (e: Throwable) {
    null
}

/**
 * Setup an Android library module with less parameters, most common configuration options are
 * already set.
 */
fun Project.androidLibraryModule(configure: AndroidLibExtension.() -> Unit) {
    val scope = AndroidLibExtension().apply(configure)

    androidBase(scope) {
        libraryVariants.all {
            val isDebugVariant = baseName.contains("debug")
            val isGenerationEnabled = isDebugVariant && scope.buildConfig

            // Enable or disable the step which generates the `BuildConfig` class
            generateBuildConfigProvider?.configure {
                enabled = isGenerationEnabled
            }
        }

        buildTypes {
            debug {
                enableUnitTestCoverage = true
                enableAndroidTestCoverage = true
            }
        }

        testOptions {
            unitTests {
                isReturnDefaultValues = true

                all { test ->
                    plugins.withId("jacoco") {
                        test.configure<JacocoTaskExtension> {
                            isIncludeNoLocationClasses = true
                            excludes = listOf("jdk.internal.*")
                        }
                    }
                }
            }
        }

        val resolvedArtifactId = scope.artifactId
        if (resolvedArtifactId != null) {
            if (scope.shouldDocument()) {
                apply(plugin = "org.jetbrains.dokka")
            }

            apply(plugin = "org.gradle.maven-publish")

            publishing {
                singleVariant("release") {
                    withJavadocJar()
                    withSourcesJar()
                }
            }

            afterEvaluate {
                extensions.configure<PublishingExtension> {
                    publications.create<MavenPublication>("release") {
                        groupId = GroupId
                        artifactId = resolvedArtifactId
                        version = VersionName

                        from(components["release"])
                    }
                }
            }
        }
    }
}

/**
 * Setup an Android app module with less parameters, most common configuration options are
 * already set.
 */
fun Project.androidAppModule(configure: AndroidAppExtension.() -> Unit) {
    val scope = AndroidAppExtension().apply(configure)

    androidBase(scope) {
        defaultConfig {
            versionCode = VersionCode
            versionName = VersionName

            vectorDrawables {
                useSupportLibrary = true
            }
        }
    }
}

/**
 * Adds a new field to the generated `BuildConfig` class, generic extension that allows to set
 * values with their original types and quotes [String] values automatically.
 */
inline fun <reified T : Any> VariantDimension.buildConfigField(field: String, value: T) {
    val typeName = T::class.java.simpleName
    val resolvedValue = if (value is String) {
        value.quoted()
    } else {
        value.toString()
    }

    buildConfigField(typeName, field, resolvedValue)
}

private inline fun <reified T : BaseExtension> Project.androidBase(
    scope: AndroidBaseExtension<T>,
    crossinline configure: T.() -> Unit,
) = android<T> {
    namespace = scope.namespace
    compileSdkVersion(CompileSdkVersion)

    defaultConfig {
        minSdk = MinSdkVersion
        targetSdk = TargetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        if (scope.isLibrary) {
            consumerProguardFiles("consumer-rules.pro")
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            val androidProguard = getDefaultProguardFile("proguard-android-optimize.txt")
            proguardFiles(androidProguard, "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()

        val arguments = mutableListOf(
            "-opt-in=kotlin.RequiresOptIn",
        )

        @Suppress("ControlFlowWithEmptyBody")
        if (scope.isLibrary) {
            // Run code specific for libraries of your project
        }

        freeCompilerArgs = freeCompilerArgs + arguments
    }

    scope.composeCompilerVersion?.let { version ->
        enableCompose(version = version)
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        maybeCreate("main").java.srcDirs(project.file("src/main/kotlin"))
        maybeCreate("test").java.srcDirs(project.file("src/test/kotlin"))
        maybeCreate("androidTest").java.srcDirs(project.file("src/androidTest/kotlin"))
    }

    packagingOptions {
        resources {
            excludes += listOf(
                "META-INF/README.md",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/{AL2.0,LGPL2.1}",
            )
        }
    }

    configure()
    scope.configureBlock(this)
}

private inline fun <reified T : BaseExtension> T.enableCompose(version: String) {
    when (this) {
        is LibraryExtension -> buildFeatures {
            compose = true
        }

        is BaseAppModuleExtension -> buildFeatures {
            compose = true
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = version
        useLiveLiterals = true
    }
}

private inline fun <reified T : BaseExtension> Project.android(configure: Action<T>) {
    (this as ExtensionAware).extensions.configure("android", configure)
}

private fun BaseExtension.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
