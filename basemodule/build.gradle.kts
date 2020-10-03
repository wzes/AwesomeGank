plugins {
    id("com.android.library")
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
            isShrinkResources = false
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
    api(Deps.Support.appCompat)
    api(Deps.Support.design)
    api(Deps.coreKtx)
    api(Deps.rxAndroid)
    api(Deps.rxKotlin2)
    api(Deps.okHttpLoggingInterceptor)
    api(Deps.Retrofit.runtime)
    api(Deps.Retrofit.rxjava2)
    api(Deps.Retrofit.gson)
    api(Deps.Glide.glide)
    api(Deps.Glide.compiler)
    api(Deps.Glide.redirect)
    api(Deps.Arouter.api)
    kapt (Deps.Arouter.compiler)
    api(Deps.LifecycleExtensions)
    api(Deps.Constraintlayout)
    api(Deps.Banner)
    api("com.github.chrisbanes:PhotoView:2.1.3")
    api(Deps.ViewPager2)
    testImplementation(Deps.Test.junit)
    androidTestImplementation(Deps.Test.runner)
    androidTestImplementation(Deps.Test.espresso)
}

repositories {
    mavenCentral()
}