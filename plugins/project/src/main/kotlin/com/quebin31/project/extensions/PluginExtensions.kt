package com.quebin31.project.extensions

import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.plugin.use.PluginDependency

val Provider<PluginDependency>.pluginId: String
    get() = get().pluginId

val ProviderConvertible<PluginDependency>.pluginId: String
    get() = asProvider().pluginId
