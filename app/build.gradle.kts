plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp) // Подключаем KSP
}

android {
    namespace = "com.example.track"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.track"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.recyclerview)
    // Компилятор аннотаций для генерации кода (используем KSP)
    ksp(libs.androidx.room.compiler)

    // Опционально: Поддержка Kotlin Coroutines и Flow
    implementation(libs.androidx.room.ktx)
}
