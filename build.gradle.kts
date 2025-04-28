// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    //Compilation KAPT
    alias(libs.plugins.org.jetbrains.kotlin.kapt) apply false

    //Dagger Hilt - Dependence Injection
    alias(libs.plugins.google.dagger.hilt) apply false
}