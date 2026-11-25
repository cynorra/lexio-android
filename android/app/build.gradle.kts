plugins {
  alias(libs.plugins.android.application)
  id("com.google.gms.google-services")
}

android {
    namespace = "com.cynorra.lexio"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.cynorra.lexio"
        minSdk = 24
        targetSdk = 36
        versionCode = 8
        versionName = "1.0.7"
    }

    signingConfigs {
        create("release") {
            val storePath = System.getenv("KEYSTORE_PATH")
            if (storePath != null) {
                storeFile = file(storePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            if (System.getenv("KEYSTORE_PATH") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      viewBinding = true
      buildConfig = true
    }
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
  // AndroidX Core
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.constraintlayout:constraintlayout:2.2.0")

  // Google Sign-In and AdMob
  implementation("com.google.android.gms:play-services-auth:21.2.0")
  implementation("com.google.android.gms:play-services-ads:24.4.0")

  // WorkManager for scheduled background notifications
  implementation("androidx.work:work-runtime:2.9.1")

  // Firebase
  implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
  implementation("com.google.firebase:firebase-auth")
  implementation("com.google.firebase:firebase-firestore")
  implementation("com.google.firebase:firebase-analytics")
  implementation("com.google.firebase:firebase-messaging")
  // User consent (GDPR/UK) before any ad request; required for EEA and UK users.
  implementation("com.google.android.ump:user-messaging-platform:3.1.0")

  // Local testing
  testImplementation("junit:junit:4.13.2")
}

