plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("kotlin-android")
}


android {
    compileSdkVersion(Build.targetSdk)
    defaultConfig {
        minSdkVersion(Build.minSdk)
        targetSdkVersion(Build.targetSdk)
        versionCode = Build.versionCode
        versionName = Build.versionName
        testInstrumentationRunner = Build.junitTest
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isZipAlignEnabled = true
            isShrinkResources = true
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    buildFeatures.dataBinding = true
    kapt {
        arguments {
            arg("AROUTER_MODULE_NAME", project.name)
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(project(":basemodule"))
    implementation(project(":photoviewer"))

    // cannot delete
    api(Deps.Arouter.api)
    kapt(Deps.Arouter.compiler)

    kapt(Deps.Room.compiler)
    api(Deps.Room.runtime)
    api(Deps.Room.ktx)
    api(Deps.Room.rxjava2)

    testImplementation(Deps.Test.junit)
    androidTestImplementation(Deps.Test.runner)
    androidTestImplementation(Deps.Test.espresso)
}
