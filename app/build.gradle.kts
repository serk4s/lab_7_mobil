import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
} else {
    logger.warn("app/google-services.json is missing. Firebase services will work after adding the file from Firebase Console.")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun localProperty(name: String): String = localProperties.getProperty(name, "")

android {
    namespace = "com.example.lab5"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.lab5"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "APPMETRICA_API_KEY", "\"${localProperty("APPMETRICA_API_KEY")}\"")
        buildConfigField("String", "YANDEX_CLIENT_ID", "\"${localProperty("YANDEX_CLIENT_ID")}\"")
        buildConfigField("String", "VK_CLIENT_ID", "\"${localProperty("VK_CLIENT_ID")}\"")
        buildConfigField("String", "MAPKIT_API_KEY", "\"${localProperty("MAPKIT_API_KEY")}\"")
        buildConfigField("Boolean", "HAS_GOOGLE_SERVICES", file("google-services.json").exists().toString())
        manifestPlaceholders["YANDEX_CLIENT_ID"] = localProperty("YANDEX_CLIENT_ID")
        manifestPlaceholders["VKIDClientID"] = localProperty("VK_CLIENT_ID")
        manifestPlaceholders["VKIDClientSecret"] = localProperty("VK_CLIENT_SECRET")
        manifestPlaceholders["VKIDRedirectHost"] = "vk.ru"
        manifestPlaceholders["VKIDRedirectScheme"] = "vk${localProperty("VK_CLIENT_ID")}"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.appmetrica.analytics)
    implementation(libs.yandex.authsdk)
    implementation(libs.yandex.mapkit)
    implementation(libs.androidx.security.crypto)
    implementation(libs.vkid)
    implementation(libs.okhttp)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
