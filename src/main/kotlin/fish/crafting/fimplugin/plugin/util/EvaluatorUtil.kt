package fish.crafting.fimplugin.plugin.util

import com.intellij.codeInsight.daemon.impl.JavaColorProvider
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypes
import fish.crafting.fimplugin.plugin.util.mc.Location
import fish.crafting.fimplugin.plugin.util.mc.Vector
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.evaluation.uValueOf

object EvaluatorUtil {

    fun getVectorConstructorType(paramCount: Int, paramType: PsiType?): VectorConstructors? {
        if(paramCount != 3) return null

        return when (paramType) {
            PsiTypes.intType() -> {
                VectorConstructors.INT_x3
            }
            PsiTypes.doubleType() -> {
                VectorConstructors.DOUBLE_x3
            }
            else -> {
                VectorConstructors.FLOAT_x3
            }
        }
    }

    fun getVector(args: List<UExpression>): Vector? {
        try {
            val type = getVectorConstructorType(args.size, args.first().getExpressionType()) ?: return null

            return when(type) {
                VectorConstructors.INT_x3 -> Vector(
                    args[0].int().toDouble(),
                    args[1].int().toDouble(),
                    args[2].int().toDouble()
                )
                VectorConstructors.DOUBLE_x3 -> Vector(
                    args[0].double(),
                    args[1].double(),
                    args[2].double()
                )
                VectorConstructors.FLOAT_x3 -> Vector(
                    args[0].float().toDouble(),
                    args[1].float().toDouble(),
                    args[2].float().toDouble()
                )
            }
        }catch (ignored: Exception) {

        }

        return null
    }

    enum class VectorConstructors {
        INT_x3, DOUBLE_x3, FLOAT_x3
    }


    fun getLocationConstructorType(paramCount: Int, paramType: PsiType?): LocationConstructors? {
        return when (paramCount) {
            4 -> LocationConstructors.WORLD_COORDS
            6 -> LocationConstructors.WORLD_COORDS_ROTATION
            else -> null
        }
    }

    fun getLocation(args: List<UExpression>): Location? {
        try {
            val type = getLocationConstructorType(args.size, args.first().getExpressionType()) ?: return null

            return when(type) {
                LocationConstructors.WORLD_COORDS -> Location(
                    args[1].double(),
                    args[2].double(),
                    args[3].double(),
                    0f, 0f,
                    args[0].stringOrNull() ?: ""
                )
                LocationConstructors.WORLD_COORDS_ROTATION -> Location(
                    args[1].double(),
                    args[2].double(),
                    args[3].double(),
                    args[4].double().toFloat(),
                    args[5].double().toFloat(),
                    args[0].stringOrNull() ?: ""
                )
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    enum class LocationConstructors {
        WORLD_COORDS,
        WORLD_COORDS_ROTATION
    }

}

fun UExpression.stringOrNull(): String? {
    val obj = this.obj()
    if (obj is String) {
        return obj
    }

    return null
}

fun UExpression.int(): Int {
    return (this.obj() as Int)
}

fun UExpression.double(): Double {
    return (this.obj() as Double)
}

fun UExpression.float(): Float {
    return (this.obj() as Float)
}

fun UExpression.obj(): Any? {
    val value = this.uValueOf()
    if (value == null) {
        return null
    }
    val constant = value.toConstant()
    if (constant == null) {
        return null
    }
    return constant.value
}