package fish.crafting.fimplugin.plugin.util

import com.intellij.openapi.actionSystem.DataKey
import com.intellij.psi.PsiElement
import fish.crafting.fimplugin.plugin.minimessage.MiniMessageInlayController
import org.jetbrains.uast.UElement
import com.intellij.openapi.util.Key;

object DataKeys {

    val PASSED_ELEMENT = dataKey<PsiElement>("fim.passed_element");
    val INLAY_CONTROLLER = key<MiniMessageInlayController>("fim.inlay_controller")
    val MM_LISTENERS_REGISTERED = key<Boolean>("fim.mm_listeners_registered")

    private fun <T> dataKey(str: String): DataKey<T> {
        return DataKey.create(str)
    }
    private fun <T> key(str: String): Key<T> {
        return Key.create<T>(str)
    }

}