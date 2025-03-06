plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
}

buildscript {
    repositories {
        google()
        maven(url = "https://plugins.gradle.org/m2/")
        mavenCentral()
        maven("https://www.jetbrains.com/intellij-repository/releases")
    }

    dependencies {
        classpath(libs.ktlint)
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
