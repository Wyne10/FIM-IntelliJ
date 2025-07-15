package fish.crafting.fimplugin.plugin.util

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import fish.crafting.fimplugin.plugin.util.javakotlin.JavaKotlinUtil
import fish.crafting.fimplugin.plugin.util.javakotlin.isJava
import fish.crafting.fimplugin.plugin.util.javakotlin.isKotlin
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.uast.UExpression


fun PsiElement.findReferencedMember(): PsiMember? = findParent(resolveReferences = true) { it is PsiClass }

fun PsiElement.findVariableParentElement(): PsiVariable? = findParent(resolveReferences = false) { it is PsiVariable }

fun PsiElement.findNewExpressionElement(): PsiNewExpression? {
    val parent: PsiElement? = findParentPredicate(resolveReferences = false) { it is PsiVariable || it is PsiNewExpression }

    if(parent is PsiNewExpression) return parent
    if(parent !is PsiVariable) return null

    for (element in parent.children) {
        if(element is PsiNewExpression) return element
    }

    return null
}

fun UExpression.modify(newExpression: String) {
    val source = this.sourcePsi ?: return
    val project = source.project

    if(source.language.isJava) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val newExpr = factory.createExpressionFromText(newExpression, source.context)
        source.replace(newExpr)
    }else if(source.language.isKotlin) {
        val factory = KtPsiFactory(project)
        val newExpr = factory.createExpression(newExpression)
        source.replace(newExpr)
    }
}

fun UExpression.add(newExpression: String) {
    val source = this.sourcePsi ?: return
    val project = source.project

    if(source.language.isJava) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val newExpr = factory.createExpressionFromText(newExpression, source.context)
        source.add(newExpr)
    }else if(source.language.isKotlin) {
        val factory = KtPsiFactory(project)
        val newExpr = factory.createExpression(newExpression)
        source.add(newExpr)
    }
}

fun PsiElement.add(newExpression: String, addCommaAndWhitespace: Boolean = false) {
    val project = this.project

    if(this.language.isJava) {
        val factory = JavaPsiFacade.getElementFactory(project)

        if(addCommaAndWhitespace) {
        }

        val newExpr = factory.createExpressionFromText(newExpression, this.context)
        this.add(newExpr)
    }else if(this.language.isKotlin) {
        val factory = KtPsiFactory(project)
        val newExpr = factory.createExpression(newExpression)
        this.add(newExpr)
    }
}

fun PsiElement.replaceWithEmptyConstructor(): PsiElement? {
    var element = this
    val newExprText = when (this) {
        is PsiMethodCallExpression -> {
            val text = methodExpression.qualifierExpression?.text ?: return null
            "new $text()"
        }

        is KtCallExpression -> {
            val text = (parent as? KtDotQualifiedExpression)?.receiverExpression?.text
            element = parent
            if (text != null) "$text()" else return null
        }

        else -> return null
    }

    return if (element.language.isJava) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val newExpr = factory.createExpressionFromText(newExprText, this)
        element.replace(newExpr)
    } else if (element.language.isKotlin) {
        val factory = KtPsiFactory(project)
        val newExpr = factory.createExpression(newExprText)
        element.replace(newExpr)
    }else null
}

fun PsiNewExpression.getExpressionList(): PsiExpressionList? {
    for (element in this.children) {
        if(element is PsiExpressionList) return element
    }

    return null
}

private inline fun <reified T : PsiElement> PsiElement.findParent(
    resolveReferences: Boolean,
    stop: (PsiElement) -> Boolean = { false },
): T? {
    var el: PsiElement = this

    while (true) {
        if (resolveReferences && el is PsiReference) {
            el = el.resolve() ?: return null
        }

        if (el is T) {
            return el
        }

        if (el is PsiFile || el is PsiDirectory || stop(el)) {
            return null
        }

        el = el.parent ?: return null
    }
}

private inline fun PsiElement.findParentPredicate(
    resolveReferences: Boolean,
    predicate: (PsiElement) -> Boolean = { false },
): PsiElement? {
    var el: PsiElement = this

    while (true) {
        if (resolveReferences && el is PsiReference) {
            el = el.resolve() ?: return null
        }

        if(predicate.invoke(el)) {
            return el
        }

        if (el is PsiFile || el is PsiDirectory ) {
            return null
        }

        el = el.parent ?: return null
    }
}