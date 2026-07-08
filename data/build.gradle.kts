import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.apollo)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
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
val paymobApiKey = localProperties.getProperty("paymob.api.key") ?: ""
val paymobPublicKey = localProperties.getProperty("paymob.public.key") ?: ""
val paymobSecretKey = localProperties.getProperty("paymob.secret.key") ?: ""
val paymobIntegrationId = localProperties.getProperty("paymob.integration.id") ?: ""
val geminiApiKey = localProperties.getProperty("gemini.api.key") ?: ""

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
        buildConfigField("String", "PAYMOB_API_KEY", "\"$paymobApiKey\"")
        buildConfigField("String", "PAYMOB_PUBLIC_KEY", "\"$paymobPublicKey\"")
        buildConfigField("String", "PAYMOB_SECRET_KEY", "\"$paymobSecretKey\"")
        buildConfigField("String", "PAYMOB_INTEGRATION_ID", "\"$paymobIntegrationId\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

apollo {
    service("shopify") {
        packageName.set("com.iti.data")
        srcDir("src/main/graphql/admin")
    }

    service("shopify-storefront") {
        packageName.set("com.iti.data.storefront")
        srcDir("src/main/graphql/storefront")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.google.play.services.location)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.apollo.runtime)
    implementation(libs.okhttp)
    implementation(libs.google.gson)
    implementation(project(":domain"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.generativeai)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.logging.interceptor)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
}