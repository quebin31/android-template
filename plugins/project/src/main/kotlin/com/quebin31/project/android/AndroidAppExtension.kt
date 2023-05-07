package com.quebin31.project.android

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

class AndroidAppExtension : AndroidBaseExtension<BaseAppModuleExtension> {
    override var namespace: String? = null
    override val isLibrary: Boolean = false
    override var composeCompilerVersion: String? = null
    override var configureBlock: BaseAppModuleExtension.() -> Unit = {}
}