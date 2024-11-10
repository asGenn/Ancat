// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    kotlin("plugin.serialization") version "2.0.20"

    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
}