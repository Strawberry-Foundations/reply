plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "1.9.10"
}

android {
    namespace = "org.strawberryfoundations.replicity"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.strawberryfoundations.replicity"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "2.0.0-beta1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)

    // Compose
    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Material icons
    implementation(libs.androidx.material.icons.extended)

    // DataStore
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)

    // Room database
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    // Lifecycle & ViewModel
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Play Services
    implementation(libs.wear.gms.playservices)

    // Browser
    implementation(libs.androidx.browser)

    // Ktor & Serialization
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Coil
    implementation(libs.coil.compose)

    // Accompanist
    implementation(libs.accompanist.flowlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug & Preview Tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.ui.test.manifest)

    ksp(libs.androidx.room.compiler)
}