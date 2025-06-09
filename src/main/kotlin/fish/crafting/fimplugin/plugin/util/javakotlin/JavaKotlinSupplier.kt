package fish.crafting.fimplugin.plugin.util.javakotlin

import com.intellij.lang.Language

class JavaKotlinSupplier<T>(
    val javaSupplier: () -> T,
    val kotlinSupplier: () -> T) {

    fun java() = javaSupplier.invoke()
    fun kotlin() = kotlinSupplier.invoke()

    fun getNotNull(language: Language) = JavaKotlinUtil.javaKotlin(language, javaSupplier, kotlinSupplier)
    fun getOrNull(language: Language) = JavaKotlinUtil.javaKotlinOrNull(language, javaSupplier, kotlinSupplier)

}