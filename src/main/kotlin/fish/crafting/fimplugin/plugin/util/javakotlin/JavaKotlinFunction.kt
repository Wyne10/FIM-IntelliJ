package fish.crafting.fimplugin.plugin.util.javakotlin

import com.intellij.lang.Language

class JavaKotlinFunction<V, K>(
    val javaFunction: (V) -> K,
    val kotlinFunction: (V) -> K) {

    fun java(input: V) = javaFunction.invoke(input)
    fun kotlin(input: V) = kotlinFunction.invoke(input)

    fun getNotNull(language: Language, input: V) = JavaKotlinUtil.javaKotlin(language,
        { javaFunction.invoke(input) },
        { kotlinFunction.invoke(input) })

    fun getOrNull(language: Language, input: V) = JavaKotlinUtil.javaKotlinOrNull(language,
        { javaFunction.invoke(input) },
        { kotlinFunction.invoke(input) })
}