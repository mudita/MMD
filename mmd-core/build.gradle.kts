plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
    id("signing")
}

kotlin {
    androidTarget {
        mavenPublishing {
            publishToMavenCentral()
            signAllPublications()

            coordinates(
                groupId = properties["library.group"].toString(),
                artifactId = properties["library.artifactId"].toString(),
                version = properties["library.version"].toString()
            )

            pom {
                name.set("Mudita MMD Library")
                description.set(
                    "A UI component library optimized for e‑ink displays on Android, " +
                        "built on top of Jetpack Compose Material 3 guidelines and classes. " +
                        "Our goal is to provide a consistent, " +
                        "predictable set of components that respects Material Design " +
                        "while addressing the specifics of e‑ink displays.",
                )
                inceptionYear.set("2025")
                url.set("https://github.com/mudita/MMD")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("krystianbroniszewskimudita")
                        name.set("Krystian Broniszewski")
                        email.set("krystian.broniszewski@mudita.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/mudita/MMD.git")
                    developerConnection.set("scm:git:ssh://github.com/mudita/MMD.git")
                    url.set("https://github.com/mudita/MMD/tree/main")
                }
            }
        }

        publishLibraryVariants("release")

        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                api(libs.androidx.compose.material3)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.compose.ui)
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

signing {
    val keyFile = rootProject.file("secring.gpg")
    val password = project.findProperty("signing.password") as? String

    if (keyFile.exists() && password != null) {
        useInMemoryPgpKeys(keyFile.readText(), password)
        sign(publishing.publications)
    }
}
