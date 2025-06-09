package fish.crafting.fimplugin.plugin.util.mc

import fish.crafting.fimplugin.plugin.util.EvaluatorUtil
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UastCallKind

data class Vector(val x: Double, val y: Double, val z: Double) {
    companion object {
        fun createFromNewExpression(newExpression: UCallExpression): Vector? {
            if (newExpression.kind === UastCallKind.Companion.CONSTRUCTOR_CALL && !newExpression.valueArguments.isEmpty()) {
                return EvaluatorUtil.getVector(newExpression.valueArguments)
            }

            return null
        }

    }

    override fun toString(): String {
        return "[$x, $y, $z]"
    }

}