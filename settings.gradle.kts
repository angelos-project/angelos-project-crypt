pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
        //maven(url = "./local-repo")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "angelos-project-crypt"

/*sourceControl {
    gitRepository(uri("https://github.com/angelos-project/angelos-project-aux.git")) {
        producesModule("angelos-project-aux:org.angproj.aux")
    }
}*/