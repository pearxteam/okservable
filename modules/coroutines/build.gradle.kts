import net.pearx.multigradle.util.invoke
import net.pearx.multigradle.util.kotlinMpp

val kotlinxCoroutinesVersion: String by project

kotlinMpp {
    metadata {
        compilations["main"] {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$kotlinxCoroutinesVersion")
                api(project(":modules:okservable"))
            }
        }
    }
    jvm {
        compilations["main"] {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                api(project(":modules:okservable"))
            }
        }
    }
    js {
        compilations["main"] {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$kotlinxCoroutinesVersion")
                api(project(":modules:okservable"))
            }
        }
    }
}