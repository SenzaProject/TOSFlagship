plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.senzaproject.tosflagship"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.senzaproject.tosflagship"
        minSdk = 26
        targetSdk = 34
        versionCode = 41
        versionName = "Update-4.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Ditandatangani dengan debug key supaya APK langsung bisa dipasang
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.9.0")
}
