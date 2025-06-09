package fish.crafting.fimplugin.plugin.util

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import fish.crafting.fimplugin.plugin.util.javakotlin.JavaKotlinFunction
import fish.crafting.fimplugin.plugin.util.mc.Location
import fish.crafting.fimplugin.plugin.util.mc.Vector
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.findContaining
import org.jetbrains.uast.toUElementOfType

object ConversionUtil {

    fun elementToVector(element: PsiElement): Vector? {
        val newExpression = element.toUElementOfType<UCallExpression>() ?: return null
        return Vector.createFromNewExpression(newExpression)
    }

    fun elementToLocation(element: PsiElement): Location? {
        val newExpression = element.toUElementOfType<UCallExpression>() ?: return null
        return Location.createFromCallExpression(newExpression)
    }

        /*JavaKotlinFunction<PsiElement, Vector?>(
        { element ->
            val newExpression = element.findContaining(UCallExpression::class.java) ?: return@JavaKotlinFunction null
            Vector.createFromNewExpression(newExpression)

            //val newExpression = element.findNewExpressionElement() ?: return@JavaKotlinFunction null
            //val expressionList = newExpression.getExpressionList() ?: return@JavaKotlinFunction null

            //Vector.createFromExpressionList(expressionList)
        },
        { element ->
            /*val argList = element.getValueArgumentList() ?: return@JavaKotlinFunction null

            Vector.createFromArgumentList(argList)*/
            null
        }).getOrNull(language, element)*/


}