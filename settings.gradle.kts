
rootProject.name = "angelos-project-crypt"

sourceControl {
    gitRepository(uri("https://github.com/angelos-project/angelos-project-aux.git")) {
        producesModule("angelos-project-aux:org.angproj.aux.util")
    }
}