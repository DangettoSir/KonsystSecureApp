plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.konsystsecureapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.konsystsecureapp"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation ("io.ktor:ktor-server-netty:1.6.4")
    implementation ("io.ktor:ktor-jackson:1.6.4")
    implementation ("io.ktor:ktor-server-host-common:1.6.4")
    implementation ("io.ktor:ktor-server-host-netty:1.6.4")
    implementation ("ch.qos.logback:logback-classic:1.2.6")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation ("org.jetbrains.exposed:exposed-core:0.34.1")
    implementation ("org.jetbrains.exposed:exposed-dao:0.34.1")
    implementation ("org.jetbrains.exposed:exposed-jdbc:0.34.1")
    implementation ("org.jetbrains.exposed:exposed-java-time:0.34.1")
    implementation ("org.postgresql:postgresql:42.3.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}