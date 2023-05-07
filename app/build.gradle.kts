import com.quebin31.project.android.androidAppModule

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.project)
    alias(libs.plugins.android.junit5)
}

androidAppModule {
    namespace = "com.quebin31.app"
    composeCompilerVersion = libs.versions.compose.compiler.get()
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.ui)

    testImplementation(libs.bundles.test)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.bundles.android.test)

    debugImplementation(libs.bundles.ui.debug)
}
