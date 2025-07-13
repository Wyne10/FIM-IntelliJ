package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.CommonTagStacks
import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import fish.crafting.fimplugin.plugin.util.ColorUtil
import java.awt.Color
import java.util.*
import javax.swing.plaf.ColorUIResource

object TransitionTagResolver : TagResolver(CommonTagStacks.COLOR) {

    override fun apply(styling: TextStyling, tag: TagContext) {
        if(tag.size == 1) return

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


        val color = getColor(colorList, phase) ?: return
        styling.color = TextStyling.SolidColorElement(color)
    }

    private fun getColor(colors: ArrayList<Color>, dPhase: Double): Color? {
        var phase = dPhase.toFloat()
        val negativePhase = phase < 0
        if(negativePhase){
            phase += 1
            colors.reverse()
        }

        val steps: Float = 1f / (colors.size - 1)
        for (colorIndex in 1..<colors.size) {
            val v = colorIndex * steps
            if (v >= phase) {
                val factor: Float = 1 + (phase - v) * (colors.size - 1)

                return if (negativePhase) {
                    ColorUtil.lerp(1 - factor, colors[colorIndex], colors[colorIndex - 1])
                } else {
                    ColorUtil.lerp(factor, colors[colorIndex - 1], colors[colorIndex])
                }
            }
        }
        return colors.firstOrNull()
    }

    override fun isValid(tag: TagContext): Boolean {
        if(tag.size == 1 && tag.first() == "transition") return true //</transition>
        if(tag.size <= 2) return false
        if(tag.first() != "transition") return false

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

}