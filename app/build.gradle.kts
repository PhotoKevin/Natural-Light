import com.android.build.api.dsl.Packaging
import org.apache.tools.ant.util.JavaEnvUtils.VERSION_11
import org.jetbrains.kotlin.com.intellij.psi.compiled.ClassFileDecompilers.Full

plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.blackholeofphotography.naturallight"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.blackholeofphotography.naturallight"
        minSdk = 28
        targetSdk = 36
        versionCode = 12
        versionName = "1.12"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}
dependencies {

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.3")
    implementation("com.github.tony19:logback-android:3.0.0")
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")

    // Java language implementation
    implementation("androidx.navigation:navigation-fragment:2.9.4")
    implementation("androidx.navigation:navigation-ui:2.9.4")

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.4")

    // Feature module Support
    implementation("com.google.android.play:feature-delivery:2.1.0")
    implementation("com.google.android.play:asset-delivery:2.3.0")
    implementation("com.google.android.play:review:2.0.2")
    implementation("com.google.android.play:app-update:2.1.0")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:2.9.4")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:2.9.4")
    implementation("androidx.navigation:navigation-fragment:2.9.4")
    implementation("androidx.navigation:navigation-ui:2.9.4")

    implementation ("androidx.preference:preference:1.2.1")
    implementation("androidx.fragment:fragment:1.8.9")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    implementation ("com.google.android.gms:play-services-maps:19.2.0")
    implementation ("org.osmdroid:osmdroid-android:6.1.20")

    // Time Shape
    implementation ("net.iakovlev:timeshape:2024a.25") {
        // Exclude standard compression library
        exclude (group= "com.github.luben", module= "zstd-jni")
        exclude (group= "net.iakovlev:timeshape:2024a.25", module= "zata.tar.*")
    }
    // Import aar for native component compilation
    implementation ("com.github.luben:zstd-jni:1.5.7-4@aar")
    implementation ("com.github.erosb:json-sKema:0.25.0")
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