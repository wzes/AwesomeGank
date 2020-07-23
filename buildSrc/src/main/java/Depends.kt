import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project


object Deps {
    object Support {
        val appCompat = "androidx.appcompat:appcompat:${Versions.support}"
        val constraint = "androidx.constraintlayout:constraintlayout:${Versions.constraint}"
        val design = "com.google.android.material:material:${Versions.design}"
    }

    object Lifecycle {
        val extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    }

    object Retrofit {
        val runtime = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        val gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
        val rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    }

    val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpLoggingInterceptor}"
    val rxKotlin2 = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin2}"
    val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"

    object Test {
        val junit = "androidx.test.ext:junit:${Versions.junit}"
        val runner = "androidx.test:runner:${Versions.runner}"
        val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    }

    object Kotlin {
        val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    }

    object Glide {
        val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
        val compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
        val redirect = "com.aminography:redirectglide:2.0.1"
    }

    object Arouter {
        val api = "com.alibaba:arouter-api:${Versions.arouterApi}"
        val compiler = "com.alibaba:arouter-compiler:${Versions.arouterCompiler}"
    }

    object Room {
        val runtime = "androidx.room:room-runtime:${Versions.room}"
        val compiler = "androidx.room:room-compiler:${Versions.room}"
        val rxjava2 = "androidx.room:room-rxjava2:${Versions.room}"
        val ktx = "androidx.room:room-ktx:${Versions.room}"
    }

    val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"


    val coreKtx = "androidx.core:core-ktx:1.3.0"

    val Banner = "com.youth.banner:banner:2.0.12"

    val LifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"


    val ViewPager2 = "androidx.viewpager2:viewpager2:1.0.0"

    fun addRepos(handler: RepositoryHandler) {
        handler.apply {
            google()
            jcenter()
            maven {
                setUrl("https://jitpack.io")
            }
            mavenCentral()
        }
    }
}

fun DependencyHandlerScope.addCommonDeps(){
    //test
    "testImplementation"(Deps.Test.junit)
    "androidTestImplementation"(Deps.Test.runner)
    "androidTestImplementation"(Deps.Test.espresso)

    "kapt"(Deps.Arouter.compiler)
    "kapt"(Deps.Room.compiler)
}