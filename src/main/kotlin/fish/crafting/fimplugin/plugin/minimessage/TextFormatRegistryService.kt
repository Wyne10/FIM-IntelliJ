package fish.crafting.fimplugin.plugin.minimessage

import com.google.gson.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import fish.crafting.fimplugin.plugin.util.ConfigUtil
import fish.crafting.fimplugin.plugin.util.toStringSet
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

@Service
class TextFormatRegistryService {
    companion object {
        private val KEY_METHODS = "methods"
        private val KEY_CLASSES = "classes"

        private val GSON = GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

        val instance: TextFormatRegistryService get() = service()

        fun methodToString(method: PsiMethod): String {
            return method.containingClass?.qualifiedName + "#" + method.name
        }

        fun classToString(clazz: PsiClass): String {
            return clazz.qualifiedName ?: ""
        }

    }

    private val validMethods = mutableSetOf<String>()
    private val validClasses = mutableSetOf<String>()

    fun isClassValid(clazz: PsiClass): Boolean = classToString(clazz) in validClasses
    fun isMethodValid(method: PsiMethod): Boolean = methodToString(method) in validMethods

    fun markMethod(method: PsiMethod, state: Boolean) {
        if(state){
            validMethods.add(methodToString(method))
        }else{
            validMethods.remove(methodToString(method))
        }

        ApplicationManager.getApplication().executeOnPooledThread {
            save(method.project)
        }
    }

    fun markClass(clazz: PsiClass, state: Boolean) {
        if(state){
            validClasses.add(classToString(clazz))
        }else{
            validClasses.remove(classToString(clazz))
        }

        ApplicationManager.getApplication().executeOnPooledThread {
            save(clazz.project)
        }
    }

    private fun addDefaults() {
        validClasses.addAll(listOf(
            "net.kyori.adventure.text.minimessage.MiniMessage",
            "com.marcusslover.plus.lib.text.Text",
            "com.marcusslover.plus.lib.item.Item",
            "net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer"
        ))
    }


    fun handleProjectOpen(project: Project) {
        validClasses.clear()
        validMethods.clear()
        addDefaults()
        load(project)
    }

    fun save(project: Project) {
        val fimDir = ConfigUtil.getFIMDir(project) ?: return
        val configFile = File(fimDir, ConfigUtil.MINIMESSAGE_FORMAT)

        val jsonObj = JsonObject()
        val methodsArray = JsonArray()
        val classesArray = JsonArray()

        validMethods.forEach { methodsArray.add(it) }
        validClasses.forEach { classesArray.add(it) }

        jsonObj.add(KEY_METHODS, methodsArray)
        jsonObj.add(KEY_CLASSES, classesArray)

        try {
            FileWriter(configFile).use { fileWriter ->
                GSON.toJson(jsonObj, fileWriter)
            }
        } catch (ignored: IOException) {}
    }

    fun load(project: Project) {
        ConfigUtil.useConfigFile(project, ConfigUtil.MINIMESSAGE_FORMAT) {
            var jsonElement: JsonElement? = null
            try{
                jsonElement = JsonParser.parseReader(FileReader(it))
            }catch (ignored: Exception) {}
            if(jsonElement == null || !jsonElement.isJsonObject) return@useConfigFile

            val jsonObject = jsonElement.asJsonObject
            if(jsonObject.has(KEY_METHODS)){
                val methodsObj = jsonObject.get(KEY_METHODS)
                if(methodsObj.isJsonArray) {
                    validMethods.clear()
                    validMethods.addAll(methodsObj.asJsonArray.toStringSet())
                }
            }

            if(jsonObject.has(KEY_CLASSES)){
                val classesObj = jsonObject.get(KEY_CLASSES)
                if(classesObj.isJsonArray) {
                    validClasses.clear()
                    validClasses.addAll(classesObj.asJsonArray.toStringSet())
                }
            }
        }
    }

}
