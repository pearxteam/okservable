import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import net.pearx.multigradle.util.MultiGradleExtension

val projectChangelog: String by project
val projectDescription: String by project

val pearxRepoUsername: String? by project
val pearxRepoPassword: String? by project
val sonatypeOssUsername: String? by project
val sonatypeOssPassword: String? by project
val githubAccessToken: String? by project
val devBuildNumber: String? by project

val props = listOf("pearxRepoUsername", "pearxRepoPassword", "pearxRepoUsername", "devBuildNumber", "signing.keyId", "signing.password", "signing.secretKeyRingFile", "multigradle.publishHostExclusivesOnly")
for(prop in props) {
    println("$prop ${project.hasProperty(prop)}")
}

plugins {
    id("net.pearx.multigradle.simple.project")
    id("org.jetbrains.kotlin.multiplatform") apply (false)
    id("com.github.breadmoirai.github-release")
    `maven-publish`
    signing
}

group = "net.pearx.okservable"
description = projectDescription

configure<MultiGradleExtension> {
    if (devBuildNumber != null) {
        projectVersion = "$projectVersion-dev-$devBuildNumber"
    }
}

configure<PublishingExtension> {
    publications.withType<MavenPublication> {
        pom {
            name.set(artifactId)
            description.set(projectDescription)
            url.set("https://github.com/pearxteam/okservable")
            licenses {
                license {
                    name.set("Mozilla Public License, Version 2.0")
                    url.set("https://mozilla.org/MPL/2.0/")
                    distribution.set("repo")
                }
            }
            organization {
                name.set("PearX Team")
                url.set("https://pearx.net/")
            }
            developers {
                developer {
                    id.set("mrAppleXZ")
                    name.set("mrAppleXZ")
                    email.set("me@pearx.net")
                    url.set("https://pearx.net/members/mrapplexz")
                    organization.set("PearX Team")
                    organizationUrl.set("https://pearx.net/")
                    roles.set(listOf("developer"))
                    timezone.set("Asia/Yekaterinburg")
                }
            }
            scm {
                url.set("https://github.com/pearxteam/okservable")
                connection.set("scm:git:git://github.com/pearxteam/okservable")
                developerConnection.set("scm:git:git://github.com/pearxteam/okservable")
            }
            issueManagement {
                system.set("GitHub")
                url.set("https://github.com/pearxteam/okservable/issues")
            }
            ciManagement {
                system.set("Jenkins")
                url.set("https://ci.pearx.net/job/pearxteam/job/okservable")
            }
        }
    }
    repositories {
        maven {
            credentials {
                username = pearxRepoUsername
                password = pearxRepoPassword
            }
            name = "pearx-repo-develop"
            url = uri("https://repo.pearx.net/maven2/develop/")
        }
        maven {
            credentials {
                username = pearxRepoUsername
                password = pearxRepoPassword
            }
            name = "pearx-repo-release"
            url = uri("https://repo.pearx.net/maven2/release/")
        }
        maven {
            credentials {
                username = sonatypeOssUsername
                password = sonatypeOssPassword
            }
            name = "sonatype-oss-release"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
    }
}

tasks {
    register("publishDevelop") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository.name.endsWith("-develop") })
    }
    register("publishRelease") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository.name.endsWith("-release") })
        dependsOn(named("githubRelease"))
    }
}

configure<SigningExtension> {
    sign(publishing.publications)
}

configure<GithubReleaseExtension> {
    setToken(githubAccessToken)
    setOwner("pearxteam")
    setRepo("okservable")
    setTargetCommitish("master")
    setBody(projectChangelog)
    //setReleaseAssets((publishing.publications["maven"] as MavenPublication).artifacts.map { it.file })
}