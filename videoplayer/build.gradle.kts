plugins {
    id("com.android.application")
}

android {
    namespace = "com.dten.videoplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dten.videoplayer"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

    implementation("androidx.leanback:leanback:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("androidx.fragment:fragment:1.5.6")
    implementation(":exoplayer")
    implementation(project(":exoplayer"))
}