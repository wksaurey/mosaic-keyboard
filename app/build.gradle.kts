import java.util.Properties

plugins {
    // AGP 9+ has built-in Kotlin support — do NOT also apply org.jetbrains.kotlin.android
    // (it fails with "extension 'kotlin' already registered").
    alias(libs.plugins.android.application)
}

// Release signing credentials live in gitignored keystore.properties (see .gitignore);
// absent the file, release builds are simply unsigned so clean checkouts still compile.
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "io.github.wksaurey.mosaic"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.wksaurey.mosaic"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "0.2.0"   // 0.2.0 = the Mosaic rename
    }

    if (keystoreProps.isNotEmpty()) {
        signingConfigs {
            create("release") {
                storeFile = file(keystoreProps["storeFile"] as String)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            // No minification: tiny app, and R8 non-determinism hurts reproducible builds.
            isMinifyEnabled = false
            if (keystoreProps.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}

// Zero dependencies, deliberately: platform APIs cover everything at minSdk 31,
// and an empty dependency graph is the strongest F-Droid/no-exfiltration posture.
dependencies {
}
