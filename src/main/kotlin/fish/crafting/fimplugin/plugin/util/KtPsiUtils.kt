package fish.crafting.fimplugin.plugin.util

import com.intellij.psi.PsiCallExpression
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiTypesUtil
import com.intellij.psi.util.siblings
import fish.crafting.fimplugin.plugin.util.javakotlin.isJava
import fish.crafting.fimplugin.plugin.util.javakotlin.isKotlin
import fish.crafting.fimplugin.plugin.util.mc.Vector
import org.apache.xml.resolver.apps.resolver
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UReferenceExpression
import org.jetbrains.uast.toUElementOfType

fun PsiClass.isString() = isClasspath("java.lang.String")

fun PsiClass.isClasspath(classpath: String): Boolean {
    val name = qualifiedName ?: return false
    return classpath == name
}

fun PsiType.getClasspath(): String? {
    val psiClass = PsiTypesUtil.getPsiClass(this) ?: return null
    return psiClass.qualifiedName
}

fun PsiMethod.isStatic(): Boolean {
    return this.hasModifierProperty(PsiModifier.STATIC)
}

fun UReferenceExpression.resolveToCallExpr(): UCallExpression? {
    val resolve = this.resolve() ?: return null

    if(resolve.language.isJava){
        val childrenOfType = resolve.getChildrenOfType<PsiCallExpression>()
        val first = childrenOfType.firstOrNull() ?: return null
        return first.toUElementOfType()
    }else if(resolve.language.isKotlin){
        val childrenOfType = resolve.getChildrenOfType<KtCallExpression>()
        val first = childrenOfType.firstOrNull() ?: return null
        return first.toUElementOfType()
    }

    return null
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