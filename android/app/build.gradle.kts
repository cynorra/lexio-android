plugins {
  alias(libs.plugins.android.application)
}

android {
    namespace = "com.lexio.app"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.lexio.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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

  // Local testing
  testImplementation("junit:junit:4.13.2")
}

