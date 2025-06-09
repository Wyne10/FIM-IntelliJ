package fish.crafting.fimplugin.plugin.util

import com.intellij.openapi.actionSystem.DataKey
import com.intellij.psi.PsiElement
import org.jetbrains.uast.UElement

object DataKeys {

    val PASSED_ELEMENT = key<PsiElement>("fim.passed_element");

    private fun <T> key(str: String): DataKey<T> {
        return DataKey.create(str)
    }

}