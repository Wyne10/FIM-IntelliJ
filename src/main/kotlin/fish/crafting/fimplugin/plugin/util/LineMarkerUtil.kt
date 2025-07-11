package fish.crafting.fimplugin.plugin.util

import com.intellij.psi.PsiCallExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiReferenceExpression
import com.intellij.psi.impl.source.tree.ElementType
import fish.crafting.fimplugin.plugin.util.javakotlin.isJava
import fish.crafting.fimplugin.plugin.util.javakotlin.isKotlin
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.uast.toUElement
import org.toml.lang.psi.ext.elementType

object LineMarkerUtil {

    fun getConstructorFromLeaf(leaf: PsiElement): PsiElement? {
        val refExpression = leaf.parent?.asReferenceExpression() ?: return null
        return refExpression.parent?.asCallExpression()
    }

}

fun PsiElement.isLeafIdentifier(): Boolean {
    if(!this.children.isEmpty()) return false //Isn't leaf

    if(language.isJava){
        return elementType == ElementType.IDENTIFIER
    }else if(language.isKotlin){
        return elementType.toString() == "IDENTIFIER" //Yes, that's how it's done.
    }

    return false
}

fun PsiElement.asCallExpression(): PsiElement? {
    return if(language.isJava) {
        this as? PsiCallExpression
    }else if(language.isKotlin) {
        this as? KtCallExpression
    }else{
        null
    }
}

fun PsiElement.asReferenceExpression(): PsiElement? {
    return if(language.isJava) {
        this as? PsiJavaCodeReferenceElement
    }else if(language.isKotlin) {
        this as? KtNameReferenceExpression
    }else{
        null
    }
}