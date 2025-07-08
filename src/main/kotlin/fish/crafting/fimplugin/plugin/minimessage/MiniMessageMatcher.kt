package fish.crafting.fimplugin.plugin.minimessage

import ai.grazie.utils.attributes.value
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.PsiExpressionList
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.util.concurrency.AppExecutorUtil

object MiniMessageMatcher {

    val validMethodCalls = hashSetOf<String>()
    val invalidMethodCalls = object : LinkedHashSet<String>() {
        override fun add(e: String): Boolean {
            if (size >= 2500) {
                val first = iterator().next()
                remove(first)
            }
            return super.add(e)
        }
    }

    fun checkForValid(call: PsiMethodCallExpression){
        val methodStr = TextFormatRegistryService.callToMethodString(call) ?: return
        val classStr = TextFormatRegistryService.callToClassString(call) ?: return

        synchronized(invalidMethodCalls){
            invalidMethodCalls.add(methodStr)
            invalidMethodCalls.add(classStr)
        }

        val resolvedMethod = call.resolveMethod()
        if (resolvedMethod == null) return
        val methodValid = TextFormatRegistryService.instance.isMethodValid(resolvedMethod)
        var classValid = false

        val containingClass = resolvedMethod.containingClass
        if(containingClass != null) {
            classValid = TextFormatRegistryService.instance.isClassValid(containingClass)
        }

        // Update cache on BGT
        synchronized(validMethodCalls) {
            synchronized(invalidMethodCalls){
                if(methodValid) {
                    invalidMethodCalls.remove(methodStr)
                    validMethodCalls.add(methodStr)
                }else{
                    invalidMethodCalls.add(methodStr)
                }

                if(classValid) {
                    invalidMethodCalls.remove(classStr)
                    validMethodCalls.add(classStr)
                }else{
                    invalidMethodCalls.add(classStr)
                }
            }
        }
    }

    fun getValidity(call: PsiMethodCallExpression): Boolean? {
        val methodStr = TextFormatRegistryService.callToMethodString(call)
        val classStr = TextFormatRegistryService.callToMethodString(call)

        if(methodStr in validMethodCalls || classStr in validMethodCalls) return true
        if(methodStr in invalidMethodCalls && classStr in invalidMethodCalls) return false
        return null
    }

    /**
     * If the user updates which methods are valid/invalid, this is all worthless now
     */
    fun clearCaches() {
        validMethodCalls.clear()
        invalidMethodCalls.clear()
    }

}

fun PsiLiteralExpression.shouldFormatMCText(): Boolean {
    val exprList = parent as? PsiExpressionList ?: return false
    val call = exprList.parent as? PsiMethodCallExpression ?: return false

    val instance = TextFormatRegistryService.instance
    if(instance.isClassValid(call) || instance.isMethodValid(call)) return true

    val validity = MiniMessageMatcher.getValidity(call)
    if(validity != null) return validity

    //Validity is null, value is not cached.
    MiniMessageMatcher.checkForValid(call)
    return MiniMessageMatcher.getValidity(call) ?: false
}