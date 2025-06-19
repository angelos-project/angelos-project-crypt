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
            implementation("org.angproj.sec:angelos-project-secrand:0.10.3")
            implementation("org.angproj.big:angelos-project-big:0.9.4")
            implementation("org.angproj.aux:angelos-project-aux:0.10.0")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}