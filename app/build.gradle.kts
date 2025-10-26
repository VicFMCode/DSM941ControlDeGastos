plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.dsm941controldegastos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.dsm941controldegastos"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    // ===========================================
    // MODIFICACIÓN CLAVE: Habilitar View Binding (XML)
    // ===========================================
    buildFeatures {
        compose = true
        viewBinding = true // Habilitar la generación de clases Binding para XML
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Contenido corregido del bloque dependencies en app/build.gradle.kts
dependencies {

    // Dependencias predeterminadas
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // ===========================================
    // DEPENDENCIAS DE ACTIVIDADES Y XML (Versiones Estables)
    // ===========================================

    // 1. Soporte para AppCompatActivity
    implementation("androidx.appcompat:appcompat:1.6.1")
    // 2. Soporte para Material Design y Vistas XML
    implementation("com.google.android.material:material:1.12.0")
    // 3. Soporte de Ciclo de Vida (Lifecycle Scope para Corrutinas en Activities)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // ===========================================
    // DEPENDENCIAS DE FIREBASE (Gestionadas por el BOM)
    // ===========================================
    // Volvemos al BOM 33.1.0 para estabilidad
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Auth Core y KTX
    implementation("com.google.firebase:firebase-auth-ktx")
    // Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Google Sign-In (Gestionada por el BOM)
    implementation("com.google.android.gms:play-services-auth")
    // Analytics (Gestionada por el BOM)
    implementation("com.google.firebase:firebase-analytics")


    // --- DEPENDENCIAS DE CORRUTINAS ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

    // --- DEPENDENCIAS ADICIONALES ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    // Pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
