package fish.crafting.fimplugin.plugin.util

import com.google.gson.JsonArray
import com.intellij.openapi.project.Project
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object ConfigUtil {

    const val MINIMESSAGE_FORMAT = "minimessage_triggers.json"

    fun getFIMDir(project: Project): File? {
        val ideaDir = Paths.get(project.basePath ?: return null, ".idea", "fim")

        val dirFile = ideaDir.toFile()
        if(dirFile.exists()) return dirFile

        try {
            val createDirectories = Files.createDirectories(ideaDir)
            return createDirectories.toFile()
        }catch (ignored: Exception) {}

        return null
    }

    fun useFIMDir(project: Project, run: (File) -> Unit) {
        getFIMDir(project)?.let(run)
    }

    fun getConfigFile(project: Project, configName: String): File? {
        val dir = getFIMDir(project) ?: return null
        val file = File(dir, configName)

        if(file.exists()) return file
        return null
    }

    fun useConfigFile(project: Project, configName: String, run: (File) -> Unit) {
        getConfigFile(project, configName)?.let(run)
    }

}

fun JsonArray.toStringSet(): HashSet<String> {
    return mapNotNull {
        if(it.isJsonPrimitive){
            if(it.asJsonPrimitive.isString){
                return@mapNotNull it.asString
            }
        }
        return@mapNotNull null
    }.toHashSet()
}