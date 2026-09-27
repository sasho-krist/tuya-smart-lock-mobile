plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "bg.sashokrist.smartlock"
    compileSdk = 35

    defaultConfig {
        applicationId = "bg.sashokrist.smartlock"
        minSdk = 26
        targetSdk = 35
        versionCode = (System.getenv("GITHUB_RUN_NUMBER") ?: "1").toInt()
        versionName = "1.0.${System.getenv("GITHUB_RUN_NUMBER") ?: "0"}"
    }

    signingConfigs {
        create("release") {
            // Постоянен ключ, за да може новите версии да се инсталират върху старите.
            // В GitHub Actions може да се подмени със secrets (вижте README).
            storeFile = file(System.getenv("SIGNING_KEYSTORE") ?: "release.keystore")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD") ?: "smartlock"
            keyAlias = System.getenv("SIGNING_KEY_ALIAS") ?: "smartlock"
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD") ?: "smartlock"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}
