buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.plugin.androidGradlePlugin)
        classpath(libs.plugin.kotlin)
    }
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            allWarningsAsErrors = false
            val args = mutableListOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.Experimental"
            )
            if (project.findProperty("app.enableComposeCompilerReports") == "true") {
                args.addAll(
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                                project.buildDir.absolutePath + "/compose_metrics"
                    )
                )
                args.addAll(
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                                project.buildDir.absolutePath + "/compose_metrics"
                    )
                )
            }
            freeCompilerArgs = args
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}