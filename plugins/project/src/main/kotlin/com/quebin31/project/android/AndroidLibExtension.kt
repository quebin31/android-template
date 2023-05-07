package com.quebin31.project.android

import com.android.build.gradle.LibraryExtension

@Suppress("MemberVisibilityCanBePrivate")
class AndroidLibExtension : AndroidBaseExtension<LibraryExtension> {
    override var namespace: String? = null
    override val isLibrary: Boolean = true
    override var composeCompilerVersion: String? = null
    var artifactId: String? = null
    var document: Boolean = true
    var buildConfig: Boolean = false
    override var configureBlock: LibraryExtension.() -> Unit = {}

    internal fun shouldDocument(): Boolean = document && artifactId != null
}