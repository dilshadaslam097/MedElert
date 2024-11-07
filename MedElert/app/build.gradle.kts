plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")

}


android {
    namespace = "com.bottech.medelert"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bottech.medelert"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
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

//    repositories {
//        mavenCentral() // This ensures access to ThreeTenABP library
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        packaging {
            resources.excludes.add("META-INF/*")
        }
    }
}

dependencies {
//    implementation 'com.android.support:multidex:2.0.1'

    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.databinding.compiler)
    implementation(libs.androidx.work.runtime.ktx)
//    implementation(libs.androidx.room.common)
//    implementation(libs.androidx.room.common.jvm)
//    implementation(libs.androidx.room.compiler)
    implementation(libs.places)

//    implementation(libs.play.services.sharing)
//    implementation(libs.play.services.drive)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
//    implementation(libs.room.ktx)
//    kapt(libs.room.compiler)
//    implementation(libs.hilt.android)
//    kapt(libs.hilt.compiler)
//    implementation(libs.threeten.bp) // ThreeTenABP dependency


}