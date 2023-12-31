import org.eclipse.jgit.internal.storage.file.FileRepository
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File
import java.util.*

private val props = Properties()
private var commitHash = ""

object Config {
    operator fun get(key: String): String? {
        val v = props[key] as? String ?: return null
        return if (v.isBlank()) null else v
    }

    fun contains(key: String) = get(key) != null

    val version: String get() = get("version") ?: commitHash
    val versionCode: Int get() = get("versionCode")?.toInt()?:1
}

class TransgenicPlugin : Plugin<Project> {
    override fun apply(project: Project) = project.applyPlugin()

    private fun Project.applyPlugin() {
        props.clear()
        rootProject.file("gradle.properties").inputStream().use { props.load(it) }
        val configPath: String? by this
        val config = configPath?.let { File(it) } ?: rootProject.file("config.prop")
        if (config.exists()) {
            config.inputStream().use { props.load(it) }

            Config["keyStore"]?.also {
                println("Key file path: $it")
                println("Key file path: ${rootProject.file(it).absolutePath}")
            }
        }

        val repo = FileRepository(rootProject.file(".git"))
        val refId = repo.refDatabase.exactRef("HEAD").objectId
        commitHash = repo.newObjectReader().abbreviate(refId, 8).name()
    }
}
