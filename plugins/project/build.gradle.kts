plugins {
    `kotlin-dsl`
}

version = "1.0.0"
group = "com.quebin31"

gradlePlugin {
    plugins {
        create("project") {
            id = "${group}.project"
            implementationClass = "com.quebin31.project.ProjectPlugin"
        }
    }
}

dependencies {
    api(libs.plugin.android)
    api(libs.plugin.kotlin)
}