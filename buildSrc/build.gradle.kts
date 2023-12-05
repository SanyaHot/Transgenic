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
//    implementation(kotlin("gradle-plugin", "1.9.10"))
    implementation(embeddedKotlin("gradle-plugin"))
    implementation("com.android.tools.build:gradle:8.2.0")
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
    implementation("org.lsposed.lsparanoid:gradle-plugin:0.5.2")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.7.0.202309050840-r")
}
