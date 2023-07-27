plugins {
    `kotlin-dsl`
}
repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("TransgenicPlugin") {
            id = "TransgenicPlugin"
            implementationClass = "TransgenicPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.8.21"))
    implementation("com.android.tools.build:gradle:8.1.0")
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.6.0")
    implementation("org.lsposed.lsparanoid:gradle-plugin:0.5.2")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")
}
