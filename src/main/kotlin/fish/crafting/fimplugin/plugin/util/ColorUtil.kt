package fish.crafting.fimplugin.plugin.util

import java.awt.Color
import kotlin.math.max
import kotlin.math.min

object ColorUtil {
    fun lerp(t: Float, a: Color, b: Color): Color {
        val clampedT = min(1.0f, max(0.0f, t)) // clamp between 0 and 1
        val ar: Int = a.red
        val br: Int = b.red
        val ag: Int = a.green
        val bg: Int = b.green
        val ab: Int = a.blue
        val bb: Int = b.blue
        return Color(
            Math.round(ar + clampedT * (br - ar)),
            Math.round(ag + clampedT * (bg - ag)),
            Math.round(ab + clampedT * (bb - ab))
        )
    }

}