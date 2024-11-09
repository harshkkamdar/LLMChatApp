plugins {
    alias(libs.plugins.android.application)
}

android {
    packaging {
        resources {
            excludes += "META-INF/gradle/incremental.annotation.processors"
            excludes += "META-INF/*"
        }
    }
    namespace = "com.hdv.llmchatapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hdv.llmchatapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.tasks.genai)
    implementation(libs.recyclerview)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

configurations {
    all {
        exclude(group = "org.jetbrains", module = "annotations")
    }
}
