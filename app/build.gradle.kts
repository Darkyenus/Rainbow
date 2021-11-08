import com.android.build.gradle.internal.dsl.BuildType

plugins {
    id("com.android.application")
}


android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "jp.rainbow"

        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode(1)
        versionName("1.0")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        fun BuildType.initDefault() {
            proguardFiles = mutableListOf(File("proguard-rules.pro"))
            isEmbedMicroApp = false
            isMinifyEnabled = true
            isDebuggable = false
        }

        getByName("release") {
            initDefault()
        }

        getByName("debug") {
            initDefault()
            isDebuggable = true

            // false: helps with debugging
            // true: more reliable, sometimes minification breaks something
            //isMinifyEnabled = false
        }
    }
}

dependencies {
    // AndroidX support libraries
    implementation("androidx.appcompat:appcompat:1.3.1")
}