import net.pearx.multigradle.plugin.modular.MultiGradleModularSettings

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

buildscript {
    val multigradleVersion: String by settings
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("net.pearx.multigradle:multigradle:$multigradleVersion")
    }
}

apply<MultiGradleModularSettings>()