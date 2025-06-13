plugins {
    `java-library`
    kotlin("multiplatform") version "1.9.25"
    `maven-publish`
}

group = "org.angproj.crypt"
version = "0.2.7"

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    explicitApi()
    jvm {
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        /*testRuns["test"].executionTask.configure {
            useJUnit()
        }*/
    }
    js(IR) {
        browser()
        nodejs()
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.angproj.aux:angelos-project-aux:0.9.8")
                implementation("org.angproj.big:angelos-project-big:0.9")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting{
            dependencies {
                implementation("org.angproj.aux:angelos-project-aux:0.9.8")
                implementation("org.angproj.big:angelos-project-big:0.9")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.bouncycastle:bcprov-jdk15on:1.70")
                implementation("org.web3j:crypto:5.0.0")
            }
        }
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting

        val baseBackboneTest by creating {
            description = "Backbone Test Suite"
            dependsOn(commonMain)
        }

        val jvmVectorTest by creating {
            description = "Vector Test Suite"
            dependsOn(jvmMain)
            dependsOn(baseBackboneTest)
        }

        jvm { compilations["test"].source(jvmVectorTest) }
    }
}

publishing {
    repositories {
        maven {}
    }
    publications {
        getByName<MavenPublication>("kotlinMultiplatform") {
            artifactId = rootProject.name
        }
    }
}