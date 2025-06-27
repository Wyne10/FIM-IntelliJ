package fish.crafting.fimplugin.plugin.util

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.siblings
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

fun PsiClass.isString() = isClasspath("java.lang.String")

fun PsiClass.isClasspath(classpath: String): Boolean {
    val name = qualifiedName ?: return false
    return classpath == name
}

fun PsiElement.getValueArgumentList(): KtValueArgumentList? {
    val parent = this.parent
    if(parent is KtValueArgumentList) return parent
    if(parent == null) return null

    val parent2 = parent.parent
    if(parent2 is KtValueArgumentList) return parent2

    //For clicking on the numbers
    if(parent2 != null){
        val parent3 = parent2.parent
        if(parent3 is KtValueArgumentList) return parent3
    }

    //val vector = Vector(...)
    //               ^
    //If the user clicks on the constructor-identifier, the parent will be a reference expression
    var callExpression: KtCallExpression? = null

    if(parent is KtNameReferenceExpression) {
        if(parent2 is KtCallExpression) callExpression = parent2
    }

    //Otherwise, they might have clicked the variable, in which case, that would be one of the siblings
    //of the variable element
    //Although, the sibling is actually a CALL_EXPRESSION, which contains the argument list.

    if(callExpression == null){
        for (sibling in this.siblings(withSelf = false)) {
            if(sibling is KtCallExpression) {
                callExpression = sibling
                break
            }
        }

    }

    if(callExpression == null) return null

    for (element in callExpression.children) {
        if(element is KtValueArgumentList) return element
    }

    return null
}