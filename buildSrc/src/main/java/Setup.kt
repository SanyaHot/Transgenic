import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.builder.internal.packaging.IncrementalPackager
import com.android.tools.build.apkzlib.sign.SigningExtension
import com.android.tools.build.apkzlib.sign.SigningOptions
import com.android.tools.build.apkzlib.zfile.ZFiles
import com.android.tools.build.apkzlib.zip.ZFileOptions
import org.apache.tools.ant.filters.FixCrLfFilter
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.filter
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registering
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.jar.JarFile
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

private fun Project.androidBase(configure: Action<BaseExtension>) =
    extensions.configure("android", configure)

private fun Project.android(configure: Action<BaseAppModuleExtension>) =
    extensions.configure("android", configure)

private fun BaseExtension.kotlinOptions(configure: Action<KotlinJvmOptions>) =
    (this as ExtensionAware).extensions.findByName("kotlinOptions")?.let {
        configure.execute(it as KotlinJvmOptions)
    }

private fun BaseExtension.kotlin(configure: Action<KotlinAndroidProjectExtension>) =
    (this as ExtensionAware).extensions.findByName("kotlin")?.let {
        configure.execute(it as KotlinAndroidProjectExtension)
    }

private val Project.android: BaseAppModuleExtension
    get() = extensions["android"] as BaseAppModuleExtension

private val Project.androidComponents
    get() = extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

fun Project.setupCommon() {
    androidBase {
        compileSdkVersion(34)
        buildToolsVersion = "34.0.0"

        defaultConfig {
            minSdk = 24
            targetSdk = 34
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        kotlinOptions {
            jvmTarget = "17"
        }

        kotlin {
            jvmToolchain(17)
        }
    }
}

private fun Project.setupAppCommon() {
    setupCommon()

    android {
        signingConfigs {
            create("config") {
                Config["keyStore"]?.also {
                    println("Hello signing")
                    storeFile = rootProject.file(it)
                    storePassword = Config["keyStorePass"]
                    keyAlias = Config["keyAlias"]
                    keyPassword = Config["keyPass"]
                }
            }
        }

        buildTypes {
            signingConfigs["config"].also {
                debug {
                    signingConfig = if (it.storeFile?.exists() == true) it
                    else signingConfigs["debug"]
                }
                release {
                    signingConfig = if (it.storeFile?.exists() == true) it
                    else signingConfigs["debug"]
                }
            }
        }

        lint {
            disable += "MissingTranslation"
            checkReleaseBuilds = false
        }

        dependenciesInfo {
            includeInApk = false
        }

        buildFeatures {
            buildConfig = true
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.4"
        }

        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
        }
    }
}

fun Project.setupApp() {
    setupAppCommon()
}
