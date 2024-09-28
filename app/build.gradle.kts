plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.safe.args)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.rvc.newsapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rvc.newsapp"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.roomKtx)
    implementation(libs.androidx.roomRuntime)
    ksp(libs.androidx.roomCompiler)
    implementation(libs.androidx.navFrag)
    implementation(libs.androidx.navUI)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.logging)
    implementation(libs.retrofit)
    implementation(libs.glide)
    ksp(libs.glide.compiler)

//    ksp ("androidx.room:room-compiler:2.6.0")
}