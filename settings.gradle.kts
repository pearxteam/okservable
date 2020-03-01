rootProject.name = "okservable"

pluginManagement {
    val multigradleVersion: String by settings
    val kotlinVersion: String by settings
    val githubReleaseVersion: String by settings

    plugins {
    	id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
    	id("net.pearx.multigradle.simple.project") version multigradleVersion
        id("com.github.breadmoirai.github-release") version githubReleaseVersion
    }
}