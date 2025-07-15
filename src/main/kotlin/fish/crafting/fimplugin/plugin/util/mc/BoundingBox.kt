package fish.crafting.fimplugin.plugin.util.mc

import com.intellij.psi.PsiModifier
import fish.crafting.fimplugin.plugin.util.EvaluatorUtil
import fish.crafting.fimplugin.plugin.util.isStatic
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UastCallKind

data class BoundingBox(val x1: Double,
                       val y1: Double,
                       val z1: Double,
                       val x2: Double,
                       val y2: Double,
                       val z2: Double) {

    companion object {
        fun createFromCallExpression(callExpression: UCallExpression): BoundingBox? {
            val args = callExpression.valueArguments
            if(args.isEmpty()) return null

            if (callExpression.kind === UastCallKind.Companion.CONSTRUCTOR_CALL) {
                return EvaluatorUtil.getBoundingBox(callExpression.valueArguments)
            }else if(callExpression.kind === UastCallKind.METHOD_CALL) {
                val resolve = callExpression.resolve() ?: return null
                if(!resolve.isStatic()) return null;

                return EvaluatorUtil.getBoundingBox(callExpression.valueArguments)
            }

            return null
        }

    }


    val centerX: Double get() = (x2 - x1) / 2.0 + x1
    val centerY: Double get() = (y2 - y1) / 2.0 + y1
    val centerZ: Double get() = (z2 - z1) / 2.0 + z1

    override fun toString(): String {
        return "[[$x1, $y1, $z1], [$x2, $y2, $z2]]"
    }
}