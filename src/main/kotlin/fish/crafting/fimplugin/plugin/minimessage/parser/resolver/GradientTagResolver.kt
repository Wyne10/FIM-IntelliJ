package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.CommonTagStacks
import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling.ColorElement
import fish.crafting.fimplugin.plugin.util.ColorUtil
import java.awt.Color
import java.util.Collections
import kotlin.math.ceil
import kotlin.math.floor

object GradientTagResolver : TagResolver(CommonTagStacks.COLOR) {

    override fun apply(styling: TextStyling, tag: TagContext) {
        if(tag.size == 1) return

        if(tag.cachedColor != null){
            styling.color = tag.cachedColor!!
            return
        }

        var end = tag.size

        var phase = tag.last().toDoubleOrNull()
        if(phase != null) end--
        else phase = 0.0

        val colorList = arrayListOf<Color>()

        for(i in 1 until end) {
            val str = tag.slices[i]
            val color = ColorTagResolver.resolveColor(str)
            if(color == null) return

            colorList.add(color)
        }

        if(colorList.isEmpty()) return

        val colors = colorList.toTypedArray()
        val color = GradientColorElement(colors, phase)
        styling.color = color
        tag.cachedColor = color
    }

    override fun isValid(tag: TagContext): Boolean {
        if(tag.size == 1 && tag.first() == "gradient") return true //</gradient>
        if(tag.size <= 2) return false
        if(tag.first() != "gradient") return false

        val phase = tag.last().toDoubleOrNull()
        val hasPhase = phase != null

        if(phase != null && (phase < -1.0 || phase > 1.0)) return false

        for (i in 1 until tag.size) {
            if(i == tag.size - 1 && hasPhase) continue

            val str = tag.slices[i]
            val color = ColorTagResolver.resolveColor(str)
            if(color == null) return false
        }

        return true
    }

    class GradientColorElement(val colors: Array<Color>, var phase: Double) : TextStyling.StrictTextColorElement() {

        init {
            if (phase < 0) {
                phase = 1 + phase // [-1, 0) -> [0, 1)
                Collections.reverse(listOf<Color>(*this.colors))
            } else {
                phase = phase
            }
            phase *= (colors.size - 1).toDouble()
        }

        fun getColor(processedLength: Int, renderIndex: Int): Color {
            val realIndex = getRealIndexAndProcess(processedLength, renderIndex)

            val position: Double = ((realIndex * getMultiplier(textLength)) + phase)
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

}