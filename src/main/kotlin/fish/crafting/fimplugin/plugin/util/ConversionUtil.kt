package fish.crafting.fimplugin.plugin.util

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import fish.crafting.fimplugin.plugin.util.javakotlin.JavaKotlinFunction
import fish.crafting.fimplugin.plugin.util.mc.BoundingBox
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

    fun elementToBoundingBox(element: PsiElement): BoundingBox? {
        val newExpression = element.toUElementOfType<UCallExpression>() ?: return null
        return BoundingBox.createFromCallExpression(newExpression)
    }


}