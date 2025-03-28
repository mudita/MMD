import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
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
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {}
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.compose.ui)
                implementation(libs.androidx.compose.material3)
                implementation(libs.androidx.compose.ui.tooling)
                implementation(libs.androidx.activity.compose)
            }
        }
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

    buildFeatures {
        compose = true
        composeOptions {
            kotlinCompilerExtensionVersion = "${properties["version.compose.extensions"]}"
        }
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
