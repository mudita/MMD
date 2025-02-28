import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
}

group = "${properties["library.group"]}"
version =
    run {
        System.getenv("GITHUB_REF_NAME")?.removePrefix("release.")
            ?: "${properties["library.version"]}"
    }

kotlin {
    android {
        publishLibraryVariants("release")

        compilations.all {
            kotlinOptions {
                kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {}
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs =
            freeCompilerArgs + listOf("-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        jvmTarget = "17"
    }
}

android {
    namespace = "${properties["library.namespace"]}"
    compileSdk = "${properties["library.compileSdk"]}".toInt()
    defaultConfig {
        minSdk = "${properties["library.minSdk"]}".toInt()
        targetSdk = "${properties["library.targetSdk"]}".toInt()
    }

    buildTypes {
        getByName("release") { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

publishing {
    repositories {
        maven {
            val properties = Properties()
            runCatching {
                val inputStream = FileInputStream("local.properties")
                properties.load(inputStream)
            }.onFailure {
                println("WARNING: Could not find `local.properties` file")
            }

            val repoUrl = properties["url"] ?: System.getenv("ARTIFACTORY_URL")
            val repoUsername = properties["username"] ?: System.getenv("ARTIFACTORY_USERNAME")
            val repoPassword = properties["password"] ?: System.getenv("ARTIFACTORY_PASSWORD")

            setUrl(repoUrl)

            credentials {
                username = repoUsername?.toString()
                password = repoPassword?.toString()
            }
        }
    }
}
