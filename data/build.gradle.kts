import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.apollo)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

val storefrontToken = localProperties.getProperty("shopify.storefront.access.token") ?: ""
val adminToken = localProperties.getProperty("shopify.admin.access.token") ?: ""
val apiKey = localProperties.getProperty("shopify.api.key") ?: ""
val apiSecretKey = localProperties.getProperty("shopify.api.secret.key") ?: ""
val storeDomain = localProperties.getProperty("shopify.store.domain") ?: "mad46-and6.myshopify.com"

android {
    namespace = "com.iti.data"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "SHOPIFY_STOREFRONT_ACCESS_TOKEN", "\"$storefrontToken\"")
        buildConfigField("String", "SHOPIFY_ADMIN_ACCESS_TOKEN", "\"$adminToken\"")
        buildConfigField("String", "SHOPIFY_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "SHOPIFY_API_SECRET_KEY", "\"$apiSecretKey\"")
        buildConfigField("String", "SHOPIFY_STORE_DOMAIN", "\"$storeDomain\"")
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
}

apollo {
    service("shopify") {
        packageName.set("com.iti.data")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.apollo.runtime)
    implementation(libs.okhttp)
    implementation(libs.google.gson)
    implementation(project(":domain"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}