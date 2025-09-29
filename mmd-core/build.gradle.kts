plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
    `maven-publish`
}

group = properties["library.group"].toString()
version =
    run {
        System.getenv("GITHUB_REF_NAME")?.removePrefix("release.")
            ?: properties["library.version"].toString()
    }

kotlin {
    androidTarget {
        mavenPublication {
            artifactId = properties["library.artifactId"].toString()
        }

        publishLibraryVariants("release")

        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    sourceSets {
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
    namespace = properties["library.namespace"].toString()
    compileSdk = properties["library.compileSdk"].toString().toInt()
    defaultConfig {
        minSdk = properties["library.minSdk"].toString().toInt()
        targetSdk = properties["library.targetSdk"].toString().toInt()
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
            kotlinCompilerExtensionVersion = properties["version.compose.extensions"].toString()
        }
    }
}
