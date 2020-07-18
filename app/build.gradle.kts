plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
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
    implementation(Deps.Support.constraint)
    implementation(Deps.Support.appCompat)
    implementation(Deps.Support.design)
    implementation(Deps.coreKtx)
    implementation(Deps.rxAndroid)
    implementation(Deps.okHttpLoggingInterceptor)
    implementation(Deps.rxKotlin2)
    implementation(Deps.Retrofit.runtime)
    implementation(Deps.Retrofit.rxjava2)
    implementation(Deps.Retrofit.gson)
    implementation(Deps.Glide.glide)
    implementation(Deps.Glide.compiler)
    implementation(Deps.Glide.redirect)
    implementation(Deps.Arouter.api)
    kapt (Deps.Arouter.compiler)

    implementation(Deps.Banner)

    testImplementation(Deps.Test.junit)
    androidTestImplementation(Deps.Test.runner)
    androidTestImplementation(Deps.Test.espresso)
}
