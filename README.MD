# Okservable [![Maven Central](https://img.shields.io/maven-central/v/net.pearx.okservable/okservable-metadata.svg?label=common&logo=kotlin&logoColor=white)](https://search.maven.org/search?q=g:%22net.pearx.okservable%22%20AND%20a:%22okservable-metadata%22) [![Maven Central](https://img.shields.io/maven-central/v/net.pearx.okservable/okservable-jvm.svg?label=jvm&logo=java&logoColor=white)](https://search.maven.org/search?q=g:%22net.pearx.okservable%22%20AND%20a:%22okservable-jvm%22) [![Maven Central](https://img.shields.io/maven-central/v/net.pearx.okservable/okservable-js.svg?label=js&logo=javascript&logoColor=white)](https://search.maven.org/search?q=g:%22net.pearx.okservable%22%20AND%20a:%22okservable-js%22)
[![License](https://img.shields.io/github/license/pearxteam/okservable.svg)](https://raw.githubusercontent.com/pearxteam/okservable/master/LICENSE.TXT)
[![Discord](https://img.shields.io/discord/136085738151346176.svg?logo=discord&logoColor=white)](https://discord.gg/q9cX9QE)
[![Release Build Status](https://img.shields.io/jenkins/build/https/ci.pearx.net/job/pearxteam/job/okservable/job/master.svg?label=build%20%5Bmaster%5D&logo=jenkins&logoColor=white)](https://ci.pearx.net/job/pearxteam/job/okservable/job/master/)
[![Develop Build Status](https://img.shields.io/jenkins/build/https/ci.pearx.net/job/pearxteam/job/okservable/job/develop.svg?label=build%20%5Bdevelop%5D&logo=jenkins&logoColor=white)](https://ci.pearx.net/job/pearxteam/job/okservable/job/develop/)

Multiplatform Kotlin library containing observable data structures, such as ObservableList, ObservableSet and ObservableMap.

# Setup
1. Add the lib to your project's dependencies by copying one of the following lines depending on the platform: 
- Groovy (replace `$okservable_version` with the version you want):
```groovy
 implementation "net.pearx.okservable:okservable-metadata:$okservable_version" // for Common
 // or
 implementation "net.pearx.okservable:okservable-jvm:$okservable_version" // for JVM
 // or
 implementation "net.pearx.okservable:okservable-js:$okservable_version" // for JS
```
- Kotlin (replace `$okservableVersion` with the version you want):
```kotlin
 implementation("net.pearx.okservable:okservable-metadata:$okservableVersion") // for Common
 // or
 implementation("net.pearx.okservable:okservable-jvm:$okservableVersion") // for JVM
 // or
 implementation("net.pearx.okservable:okservable-js:$okservableVersion") // for JS
```

2. Use the library and have fun!

# Examples
 - Creating a simple observable list:
```kotlin
//             \/ there may be anything extending MutableList
val items = mutableListOf("pickaxe", "bow").observableListSimple {
    println("The list has modified!")
}
items += "axe"     // The list has modified!
items -= "pickaxe" // The list has modified!
items -= "teapot"  // This line won't print the message since the list wasn't modified
```
 - Creating a simple observable set:
```kotlin
//             \/ there may be anything extending MutableSet
val items = mutableSetOf("pickaxe", "bow").observableSetSimple {
    println("The set has modified!")
}
items += "axe"     // The set has modified!
items += "pickaxe" // This line won't print the message since the set wasn't modified
```
- Creating a simple observable collection:
```kotlin
//             \/ there may be anything extending MutableCollection
val items = mutableListOf("pickaxe", "bow").observableCollectionSimple {
    println("The collection has modified!")
}
items += "axe"     // The collection has modified!
```
- Creating a simple observable map:
```kotlin
//             \/ there may be anything extending MutableMap
val items = mutableMapOf("pickaxe" to "diamond", "axe" to "iron").observableMapSimple {
    println("The map has modified!")
}
items["pickaxe"] = "stone" // The map has modified!
items["bow"] = "wooden"    // The map has modified!
```
- Creating an observable list:
```kotlin
//             \/ there may be anything extending MutableList
val items = mutableListOf("pickaxe", "bow").observableList {
    add { index, element ->
        println("add[$index]: $element")
    }
    remove { index, element ->
        println("remove[$index]: $element")
    }
    clear { elements ->
        println("clear: $elements")
    }
    set { index, prevElement, newElement ->
        println("set[$index]: $prevElement to $newElement")
    }
}
items += "axe"                    // add[2]: axe
items -= "pickaxe"                // remove[0]: pickaxe
items -= "teapot"                 // This line won't print any message since the list wasn't modified
items += setOf("shovel", "sword") // add[2]: shovel
                                  // add[3]: sword
items[2] = "not_shovel"           // set[2]: shovel to not_shovel
items.clear()                     // clear: [bow, axe, not_shovel, sword]
```

 - Creating an observable set:
```kotlin
//             \/ there may be anything extending MutableSet
val items = mutableSetOf("pickaxe", "bow").observableSet {
    add { element ->
        println("add: $element")
    }
    remove { element ->
        println("remove: $element")
    }
    clear { elements ->
        println("clear: $elements")
    }
}
items += "axe"     // add: axe
items += "pickaxe" // This line won't print any message since the set wasn't modified
items -= "pickaxe" // remove: pickaxe
items.clear()      // clear: [bow, axe]
```
- Creating an observable collection:
```kotlin
//             \/ there may be anything extending MutableCollection
val items = mutableListOf("pickaxe", "bow").observableCollection {
    add { element ->
        println("add: $element")
    }
    remove { element ->
        println("remove: $element")
    }
    clear { elements ->
        println("clear: $elements")
    }
}
items += "axe"     // add: axe
items -= "pickaxe" // remove: pickaxe
items.clear()      // clear: [bow, axe]
```
- Creation an observable map:
```kotlin
//             \/ there may be anything extending MutableMap
val items = mutableMapOf("pickaxe" to "diamond", "axe" to "iron").observableMap {
    put { key, prevValue, value ->
        println("put[$key]: ${prevValue ?: "<null>"} to $value")
    }
    remove { key, value ->
        println("remove[$key]: $value")
    }
    clear { elements ->
        println("clear: $elements")
    }
}
items["pickaxe"] = "stone" // put[pickaxe]: diamond to stone
items["bow"] = "wooden"    // put[bow]: <null> to wooden
items.remove("axe")        // remove[axe]: iron
items.clear()              // clear: {pickaxe=stone, bow=wooden}
```
