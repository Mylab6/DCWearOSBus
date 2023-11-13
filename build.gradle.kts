plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    // Ensure the Kotlin Serialization plugin version is compatible with your Kotlin version
    kotlin("plugin.serialization") version "1.8.10" apply false

}
