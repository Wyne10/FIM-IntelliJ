package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.CommonTagStacks
import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.GradientTagResolver.GradientColorElement
import java.awt.Color

object RainbowTagResolver : TagResolver(CommonTagStacks.COLOR) {

    override fun apply(styling: TextStyling, tag: TagContext) {
        if(tag.cachedColor != null){
            styling.color = tag.cachedColor!!
            return
        }

        var phase = 0
        var invert = false

        if(tag.size == 2) {
            var last = tag.last()
            invert = last.startsWith("!")
            if(invert && last.length > 1) {
                last = last.substring(1)
            }

            phase = last.toIntOrNull() ?: return
        }

        val color = RainbowColorElement(phase, invert)
        styling.color = color
        tag.cachedColor = color
    }

    override fun isValid(tag: TagContext): Boolean {
        if(tag.size > 2) return false
        if(tag.first() != "rainbow") return false

        if(tag.size == 1) return true

        var phaseText = tag.last()
        if(phaseText.isEmpty()) return false;
        if(phaseText[0] == '!') {
            if(phaseText.length == 1) return true

            phaseText = phaseText.substring(1)
        }

        return phaseText.toIntOrNull() != null
    }

    class RainbowColorElement private constructor(val phase: Double, val reverse: Boolean) : TextStyling.StrictTextColorElement() {
        /**
         * Computes the phase to / 10.0 !!
         */
        constructor(phase: Int, reverse: Boolean) : this(phase / 10.0, reverse)

        fun getColor(processedLength: Int, renderIndex: Int): Color {
            var realIndex = getRealIndexAndProcess(processedLength, renderIndex).toFloat()
            if(reverse) {
               realIndex = textLength - realIndex - 1
            }

            val hue = ((realIndex / textLength + phase) % 1f).toFloat()
            return Color(Color.HSBtoRGB(hue, 1f, 1f))
        }

    }

}