package fish.crafting.fimplugin.plugin.util.mc

import fish.crafting.fimplugin.plugin.util.EvaluatorUtil
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UastCallKind

data class Location(val x: Double,
                    val y: Double,
                    val z: Double,
                    val pitch: Float, val yaw: Float,
                    var world: String = "") {

    companion object {
        private val VALID_METHOD_RETURNS = setOf("toLocation")

        fun createFromCallExpression(callExpression: UCallExpression): Location? {
            val args = callExpression.valueArguments
            if(args.isEmpty()) return null;

            if (callExpression.kind === UastCallKind.Companion.CONSTRUCTOR_CALL) {
                return EvaluatorUtil.getLocation(callExpression.valueArguments)
            }else if(callExpression.kind === UastCallKind.METHOD_CALL) {
                val methodName = callExpression.methodName ?: return null

                if(methodName in VALID_METHOD_RETURNS) {
                    //TODO make vector.toLocation(world) work
                }
            }

            return null
        }

    }

    override fun toString(): String {
        return "[$x, $y, $z, ($pitch, $yaw), in '$world']"
    }
}