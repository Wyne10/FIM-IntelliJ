package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpressionList
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.AppExecutorUtil
import fish.crafting.fimplugin.plugin.util.javakotlin.isJava
import fish.crafting.fimplugin.plugin.util.javakotlin.isKotlin
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.toUElement

fun PsiElement.checkMCFormatAndRun(controller: MiniMessageInlayController, run: (Boolean) -> Unit) {
    if(quickShouldFormatMCText(controller)) {
        run.invoke(true)
        return
    }

    ReadAction.nonBlocking<Boolean> { shouldFormatMCText() }
        .finishOnUiThread(ApplicationManager.getApplication().defaultModalityState, run)
        .submit(AppExecutorUtil.getAppExecutorService())
}

fun PsiElement.getLiteralValue(): Any? {
    return when(this) {
        is PsiLiteralExpression -> this.value
        is KtStringTemplateExpression -> this.entries.joinToString("") { it.text }
        else -> ""
    }
}

fun PsiElement.getParentLiteral(): PsiElement? {
    val clazz = if(this.language.isJava) PsiLiteralExpression::class.java
    else if(this.language.isKotlin) KtStringTemplateExpression::class.java
    else return null

    return PsiTreeUtil.getParentOfType(this, clazz, false)
}

fun PsiFile.visitExpressions(run: (PsiElement) -> Unit) {
    if(language.isJava) {
        accept(object : JavaRecursiveElementVisitor() {
            override fun visitLiteralExpression(expression: PsiLiteralExpression) {
                run.invoke(expression)
            }
        })
    }else if(language.isKotlin){
        accept(object : KtTreeVisitorVoid() {
            override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
                run.invoke(expression)
            }
        })
    }
}

private fun PsiElement.resolveMethodFromLiteral(): PsiMethod? {
    if(this.language.isJava){
        val exprList = parent as? PsiExpressionList ?: return null
        val call = exprList.parent as? PsiMethodCallExpression ?: return null
        return call.resolveMethod()
    }else if(this.language.isKotlin){
        val valArg = parent as? KtValueArgument ?: return null
        val exprList = valArg.parent as? KtValueArgumentList ?: return null
        val call = exprList.parent as? KtCallExpression ?: return null

        val uElement = call.toUElement() as? UCallExpression ?: return null
        return uElement.resolve()
    }

    return null
}

/**
 * This method checks whether the Literal Expression is being edited right now.
 */
fun PsiElement.quickShouldFormatMCText(controller: MiniMessageInlayController): Boolean {
    //We are editing this rn
    return controller.matchesCached(this)
}

fun PsiElement.shouldFormatMCText(): Boolean {
    val method = resolveMethodFromLiteral() ?: return false
    if(TextFormatRegistryService.instance.isMethodValid(method)) return true

    val containingClass = method.containingClass ?: return false
    return TextFormatRegistryService.instance.isClassValid(containingClass)
}