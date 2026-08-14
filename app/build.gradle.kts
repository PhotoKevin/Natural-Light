import com.android.build.api.dsl.Packaging
import org.apache.tools.ant.util.JavaEnvUtils.VERSION_11
//import org.jetbrains.kotlin.com.intellij.psi.compiled.ClassFileDecompilers.Full

plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.blackholeofphotography.naturallight"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.blackholeofphotography.naturallight"
        minSdk = 28
        targetSdk = 37
        versionCode = 18
        versionName = "1.18"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            // Enables code-related app optimization.
            isMinifyEnabled = true

            // Enables resource shrinking.
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "data.tar.zstdX"
        }
    }
    compileSdkMinor = 0
}
dependencies {
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp.android)
    implementation(libs.okhttp)
    implementation(libs.jakarta.annotation.api)
    implementation(libs.javax.annotation.api)
    implementation(libs.jackson.databind.nullable)
//    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.gson.fire)
    implementation(libs.powermock.api.easymock)
    implementation(libs.preference)
    implementation(libs.play.services.location)
    implementation(libs.recyclerview)
    implementation(libs.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.powermock.module.junit4)
    implementation(libs.work.runtime)
    implementation(libs.mapsforge.core)
    implementation(libs.mapsforge.map)
    implementation(libs.mapsforge.map.reader)
    implementation(libs.mapsforge.themes)
    implementation(libs.mapsforge.map.android)
    implementation(libs.androidsvg)
    implementation(libs.slf4j.api)
    implementation(libs.logback.android)
    implementation (libs.json.skema)

    implementation(libs.material)

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:2.9.8")

    // Jetpack Compose Integration
    implementation(libs.androidx.navigation.compose)

    implementation("androidx.fragment:fragment:1.9.0")
    implementation ("com.google.android.gms:play-services-maps:20.0.0")

    // Time Shape
    implementation ("net.iakovlev:timeshape:2025b.28") {
        // Exclude standard compression library
        exclude (group= "com.github.luben", module= "zstd-jni")
        exclude (group= "net.iakovlev:timeshape:2024a.25", module= "zata.tar.*")
    }

    // Import aar for native component compilation
    implementation ("com.github.luben:zstd-jni:1.5.7-13@aar")
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
}


secrets {
    // Change the properties file from the default "local.properties" in your root project
    // to another properties file in your root project.
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be checked in version
    // control.
    defaultPropertiesFileName = "secrets.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}