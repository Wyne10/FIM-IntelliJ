package fish.crafting.fimplugin.plugin.minimessage.parser

import fish.crafting.fimplugin.plugin.util.ColorUtil
import java.awt.Color
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class TextStyling(var color: ColorElement = SolidColorElement(Color.WHITE),
                  var shadowColor: Color? = null,
                  var bold: Boolean = false,
                  var italic: Boolean = false,
                  var underlined: Boolean = false,
                  var strikethrough: Boolean = false,
                  var obfuscated: Boolean = false) {

    companion object{
        val default get() = TextStyling()
    }

    fun getShadow(baseColor: Color): Color {
        return shadowColor ?: Color(baseColor.red shr 2, baseColor.green shr 2, baseColor.blue shr 2)
    }

    fun clone() = TextStyling(color, shadowColor, bold, italic, underlined, strikethrough, obfuscated)

    fun resetDecorators() {
        bold = false
        italic = false
        underlined = false
        strikethrough = false
        obfuscated = false
    }

    class GradientColorElement(val colors: Array<Color>, var phase: Double) : ColorElement() {

        init {
            if (phase < 0) {
                phase = 1 + phase // [-1, 0) -> [0, 1)
                Collections.reverse(listOf<Color>(*this.colors))
            } else {
                phase = phase
            }
            phase *= (colors.size - 1).toDouble()
        }

        fun getColor(textLength: Int, index: Int): Color {
            val position: Double = ((index * getMultiplier(textLength)) + phase)
            val lowUnclamped = floor(position).toInt()

            val high = ceil(position).toInt() % this.colors.size
            val low = lowUnclamped % this.colors.size

            return ColorUtil.lerp(position.toFloat() - lowUnclamped, this.colors[low], this.colors[high])
        }

        private fun getMultiplier(length: Int): Double {
            return if (length == 1) {
                0.0
            } else {
                (colors.size - 1).toDouble() / (length - 1)
            }
        }
    }

    class SolidColorElement(val color: Color) : ColorElement() {

    }

    open class ColorElement {

    }
}