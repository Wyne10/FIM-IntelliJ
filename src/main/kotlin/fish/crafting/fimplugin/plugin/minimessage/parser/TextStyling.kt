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

    class SolidColorElement(val color: Color) : ColorElement() {

    }

    /**
     * Color element that keeps an index-track of the whole text, regardless if it is the top tag.
     * e.g.
     * <rainbow> 0123 <red>456 </red> 789 </rainbow>
     * <rainbow> keeps track of the index of each letter, even the ones in <red>.
     *
     * Used to make strict gradients/transitions
     */
    open class StrictTextColorElement : LengthTrackingColorElement() {
        //Width of already processed/rendered text
        var processedWidth = 0
        private var rIndex = 0

        protected fun getRealIndexAndProcess(processedLength: Int, renderIndex: Int): Int {
            if(rIndex != renderIndex) {
                rIndex = renderIndex
                processedWidth = 0
            }

            val realIndex = processedWidth
            processedWidth += processedLength

            return realIndex
        }
    }

    /**
     * Tracks length of the targeted text
     */
    open class LengthTrackingColorElement : ColorElement() {
        var textLength = 0
    }

    open class ColorElement {

    }

    override fun toString(): String {
        return "[Color: $color, Shadow: $shadowColor]"
    }
}