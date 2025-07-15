package fish.crafting.fimplugin.plugin.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiCallExpression
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.UastCallKind
import org.jetbrains.uast.toUElementOfType

object ReplaceUtil {

    fun modifyVector(callExpr: UCallExpression, x: Double, y: Double, z: Double, kotlin: Boolean) {
        ApplicationManager.getApplication().invokeLater {
            if(kotlin){
                ReadAction.run<RuntimeException> {
                    modifyVector0(callExpr, x, y, z)
                }
            }else{
                modifyVector0(callExpr, x, y, z)
            }
        }
    }

    private fun modifyVector0(callExpr: UCallExpression, x: Double, y: Double, z: Double){
        if(callExpr.kind != UastCallKind.CONSTRUCTOR_CALL) return
        val source = callExpr.sourcePsi ?: return

        WriteCommandAction.runWriteCommandAction(source.project) {
            val args = callExpr.valueArguments
            if(args.size == 3) {
                args[0].modify(x.toString())
                args[1].modify(y.toString())
                args[2].modify(z.toString())
            }
        }
    }

    fun modifyLocation(callExpr: UCallExpression, x: Double, y: Double, z: Double, pitch: Float, yaw: Float, kotlin: Boolean) {
        ApplicationManager.getApplication().invokeLater {
            if(kotlin){
                ReadAction.run<RuntimeException>{
                    modifyLocation0(callExpr, x, y, z, pitch, yaw)
                }
            }else{
                modifyLocation0(callExpr, x, y, z, pitch, yaw)
            }
        }
    }

    private fun modifyLocation0(callExpr: UCallExpression, x: Double, y: Double, z: Double, pitch: Float, yaw: Float){
        if(callExpr.kind != UastCallKind.CONSTRUCTOR_CALL) return
        val source = callExpr.sourcePsi ?: return

        WriteCommandAction.runWriteCommandAction(source.project) {
            val args = callExpr.valueArguments

            //Janky implementation, but it should work!
            val noWorldParam = args.size == 3 || args.size == 5 //Minestom

            var index = if(noWorldParam) 0 else 1

            addOrModify(callExpr, args, index++, x.toString(), doublePredicate(x))
            addOrModify(callExpr, args, index++, y.toString(), doublePredicate(y))
            addOrModify(callExpr, args, index++, z.toString(), doublePredicate(z))
            addOrModify(callExpr, args, index++, yaw.toString() + "f", doublePredicate(yaw.toDouble()))
            addOrModify(callExpr, args, index, pitch.toString() + "f", doublePredicate(pitch.toDouble()))
        }
    }

    fun modifyBoundingBox(callExpr: UCallExpression,
                          x1: Double, y1: Double, z1: Double,
                          x2: Double, y2: Double, z2: Double,
                          kotlin: Boolean) {
        ApplicationManager.getApplication().invokeLater {
            if(kotlin){
                ReadAction.run<RuntimeException>{
                    modifyBoundingBox0(callExpr, x1, y1, z1, x2, y2, z2)
                }
            }else{
                modifyBoundingBox0(callExpr, x1, y1, z1, x2, y2, z2)
            }
        }
    }

    private fun modifyBoundingBox0(callExpr: UCallExpression,
                                   x1: Double, y1: Double, z1: Double,
                                   x2: Double, y2: Double, z2: Double){
        val replaceWithConstructor = when (callExpr.kind) {
            UastCallKind.METHOD_CALL -> { //.of
                true
            }
            UastCallKind.CONSTRUCTOR_CALL -> {
                false
            }
            else -> {
                return
            }
        }

        var source = callExpr.sourcePsi ?: return
        var aCallExpr = callExpr

        WriteCommandAction.runWriteCommandAction(source.project) {
            if(replaceWithConstructor) {
                source = source.replaceWithEmptyConstructor() ?: return@runWriteCommandAction
                aCallExpr = source.toUElementOfType<UCallExpression>() ?: return@runWriteCommandAction
            }

            val args = aCallExpr.valueArguments

            var index = 0

            addOrModify(aCallExpr, args, index++, x1.toString(), doublePredicate(x1))
            addOrModify(aCallExpr, args, index++, y1.toString(), doublePredicate(y1))
            addOrModify(aCallExpr, args, index++, z1.toString(), doublePredicate(z1))
            addOrModify(aCallExpr, args, index++, x2.toString(), doublePredicate(x2))
            addOrModify(aCallExpr, args, index++, y2.toString(), doublePredicate(y2))
            addOrModify(aCallExpr, args, index++, z2.toString(), doublePredicate(z2))
        }
    }


    private fun addOrModify(callExpr: UCallExpression,
                            args: List<UExpression>,
                            index: Int,
                            newExpression: String,
                            isSamePredicate: ((UExpression) -> Boolean)? = null){
        if(index >= args.size) {
            val sourcePsi = callExpr.sourcePsi ?: return

            if(sourcePsi is PsiCallExpression) {
                val list = sourcePsi.argumentList ?: return
                list.add(newExpression)

            }else if(sourcePsi is KtCallExpression){
                val factory = KtPsiFactory(sourcePsi.project)
                val newExpr = factory.createArgument(newExpression)

                val childrenOfType = sourcePsi.getChildrenOfType<KtValueArgumentList>()
                val valArgs = childrenOfType.firstOrNull()
                valArgs?.addArgument(newExpr)
            }

        }else{
            if(isSamePredicate != null && isSamePredicate.invoke(args[index])) {
                return
            }

            args[index].modify(newExpression)
        }
    }
}


private fun doublePredicate(double: Double): (UExpression) -> Boolean = {
    var b = false
    try{
        if(it.double() == double) {
            b = true
        }

    }catch (e: Exception) {
    }

    b
}
