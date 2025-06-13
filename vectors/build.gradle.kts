plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

group = "org.angproj.crypt"
version = "1.0-SNAPSHOT"

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    jvm()
    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        jvmTest.dependencies {
            api(project(":library"))
            implementation("org.angproj.aux:angelos-project-aux:0.9.8")
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.mockito)
        }
    }
}

android {
    namespace = group.toString()
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    /*compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }*/
}