package com.quebin31.project.android

import com.android.build.gradle.BaseExtension

interface AndroidBaseExtension<T : BaseExtension> {
    var namespace: String?
    var composeCompilerVersion: String?
    var configureBlock: T.() -> Unit
    val isLibrary: Boolean

    fun configure(block: T.() -> Unit) {
        configureBlock = block
    }
}