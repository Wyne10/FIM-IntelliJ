package fish.crafting.fimplugin.plugin.util.javakotlin

import com.intellij.lang.Language

internal object JavaKotlinUtil {

    fun <T> javaKotlinOrNull(language: Language, java: () -> T, kotlin: () -> T): T? {
        return if(language.isJava) {
            java.invoke()
        }else if(language.isKotlin) {
            kotlin.invoke()
        }else {
            null
        }
    }

    fun <T> javaKotlin(language: Language, java: () -> T, kotlin: () -> T): T {
        return if(language.isJava) {
            java.invoke()
        }else {
            kotlin.invoke()
        }
    }

}

val Language.isJava: Boolean
    get() = this.isKindOf("JAVA")

val Language.isKotlin: Boolean
    get() = this.isKindOf("kotlin")

val Language.isJson: Boolean
    get() = this.isKindOf("JSON")

val Language.isYaml: Boolean
    get() = this.isKindOf("yaml")