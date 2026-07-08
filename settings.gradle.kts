pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
        maven { url = java.net.URI("https://paymob-sdk.s3.eu-central-1.amazonaws.com/maven") }
        maven { url = rootProject.projectDir.toURI().resolve("libs") }
        maven { url = rootProject.projectDir.toURI().resolve("app/libs") }
        maven { url = rootProject.projectDir.toURI().resolve("presentation/libs") }
    }
}

rootProject.name = "ShopIQ"
include(":presentation")
include(":data")
include(":domain")
include(":app")
