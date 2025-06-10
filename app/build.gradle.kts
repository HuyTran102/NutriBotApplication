plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.huytran.goodlife"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.huytran.goodlifes"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.1.1"

        resConfigs("en", "vi")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false;
            isShrinkResources = false;
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildToolsVersion = "34.0.0"
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("com.google.firebase:firebase-firestore:25.1.4")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.github.leandroborgesferreira:loading-button-android:2.3.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("com.github.phuquy2114:Libraries_android:1.0.9")
    implementation("me.relex:circleindicator:2.1.6")
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")
    // CameraView for quick camera usage (optional library by Google)
    implementation("com.otaliastudios:cameraview:2.7.2")
    implementation("com.google.mlkit:image-labeling:17.0.9")
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.13.0")
}