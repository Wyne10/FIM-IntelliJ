package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import com.intellij.ui.ColorHexUtil
import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

object GradientTagResolver : TagResolver() {

    override fun apply(styling: TextStyling, tag: TagContext) {
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
        styling.color = TextStyling.GradientColorElement(colors, phase)
    }

    override fun isValid(tag: TagContext): Boolean {
        if(tag.size == 1 && tag.first() == "gradient") return true //</gradient>
        if(tag.size <= 2) return false
        if(tag.first() != "gradient") return false

        val hasPhase = tag.last().toDoubleOrNull() != null

        for (i in 1 until tag.size) {
            if(i == tag.size - 1 && hasPhase) continue

            val str = tag.slices[i]
            val color = ColorTagResolver.resolveColor(str)
            if(color == null) return false
        }

        return true
    }

}