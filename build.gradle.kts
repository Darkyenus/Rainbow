
// Build-script only dependencies
buildscript {
    repositories {
        mavenCentral()
        google()

        gradlePluginPortal() // Here just for some transitive dependencies that used to be in jcenter
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")


        // Required here, because linter uses resolution scope of projects and not build script :(
        // Try to remove after build script updates in some time
        gradlePluginPortal()
    }
}
