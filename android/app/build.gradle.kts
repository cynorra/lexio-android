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
        versionCode = 2
        versionName = "1.0.1"
    }

    buildTypes {
        release {
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
}

dependencies {
  // AndroidX Core
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.constraintlayout:constraintlayout:2.2.0")

  // Litho & Yoga for declarative UI in Java
  implementation("com.facebook.litho:litho-core:0.50.2")
  implementation("com.facebook.litho:litho-widget:0.50.2")
  annotationProcessor("com.facebook.litho:litho-processor:0.50.2")
  implementation("com.facebook.soloader:soloader:0.11.0")

  // Fresco for image loading
  implementation("com.facebook.fresco:fresco:3.1.3")

  // Lottie for animations
  implementation("com.airbnb.android:lottie:6.4.0")

  // Java-WebSocket
  implementation("org.java-websocket:Java-WebSocket:1.5.7")

  // Spectrum for image transcoding
  implementation("com.facebook.spectrum:spectrum-default:1.3.0")

  // Google Sign-In, AdMob, and OkHttp client
  implementation("com.google.android.gms:play-services-auth:21.2.0")
  implementation("com.google.android.gms:play-services-ads:23.1.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")

  // Firebase
  implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
  implementation("com.google.firebase:firebase-auth")
  implementation("com.google.firebase:firebase-firestore")

  // Gson for JSON parsing
  implementation("com.google.code.gson:gson:2.10.1")

  // Local testing
  testImplementation("junit:junit:4.13.2")
}

