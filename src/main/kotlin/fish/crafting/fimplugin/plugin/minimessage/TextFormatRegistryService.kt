package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression

@Service
class TextFormatRegistryService {
    companion object {
        val instance: TextFormatRegistryService get() = service()

        fun methodToString(method: PsiMethod): String {
            return method.containingClass?.qualifiedName + "#" + method.name
        }

        fun classToString(clazz: PsiClass): String {
            return clazz.qualifiedName ?: ""
        }

    }

    private val validMethods = mutableSetOf<String>()
    private val validClasses = mutableSetOf<String>()

    fun isClassValid(clazz: PsiClass): Boolean = classToString(clazz) in validClasses
    fun isMethodValid(method: PsiMethod): Boolean = methodToString(method) in validMethods

    fun markMethod(method: PsiMethod, state: Boolean) {
        if(state){
            validMethods.add(methodToString(method))
        }else{
            validMethods.remove(methodToString(method))
        }
    }

    fun markClass(clazz: PsiClass, state: Boolean) {
        if(state){
            validClasses.add(classToString(clazz))
        }else{
            validClasses.remove(classToString(clazz))
        }
    }


}
