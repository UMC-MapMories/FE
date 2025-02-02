plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.devdi.mapmories"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.devdi.mapmories"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

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
    buildFeatures{
        viewBinding=true
    }
    dataBinding {
        enable = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.facebook.android:facebook-android-sdk:12.3.0")

    implementation("com.google.android.gms:play-services-maps:18.1.0") // Google Maps
    // implementation("com.google.android.gms:play-services:12.0.1") // Google Fused Location Provider
    implementation("com.google.android.gms:play-services-location:21.3.0") // Google Geocoding
    // implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}