package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import com.intellij.ui.ColorHexUtil
import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

object PrideTagResolver : TagResolver() {

    private val colorMap = mapOf(
        // Colours taken from https://www.kapwing.com/resources/official-pride-colors-2021-exact-color-codes-for-15-pride-flags.
        "pride" to colorArrayOf(0xE50000, 0xFF8D00, 0xFFEE00, 0x28121, 0x004CFF, 0x770088),
        "progress" to colorArrayOf(0xFFFFFF, 0xFFAFC7, 0x73D7EE, 0x613915, 0x000000, 0xE50000, 0xFF8D00, 0xFFEE00, 0x28121, 0x004CFF, 0x770088),
        "trans" to colorArrayOf(0x5BCFFB, 0xF5ABB9, 0xFFFFFF, 0xF5ABB9, 0x5BCFFB),
        "bi" to colorArrayOf(0xD60270, 0x9B4F96, 0x0038A8),
        "pan" to colorArrayOf(0xFF1C8D, 0xFFD700, 0x1AB3FF),
        "nb" to colorArrayOf(0xFCF431, 0xFCFCFC, 0x9D59D2, 0x282828),
        "lesbian" to colorArrayOf(0xD62800, 0xFF9B56, 0xFFFFFF, 0xD462A6, 0xA40062),
        "ace" to colorArrayOf(0x000000, 0xA4A4A4, 0xFFFFFF, 0x810081),
        "agender" to colorArrayOf(0x000000, 0xBABABA, 0xFFFFFF, 0xBAF484, 0xFFFFFF, 0xBABABA, 0x000000),
        "demisexual" to colorArrayOf(0x000000, 0xFFFFFF, 0x6E0071, 0xD3D3D3),
        "genderqueer" to colorArrayOf(0xB57FDD, 0xFFFFFF, 0x49821E),
        "genderfluid" to colorArrayOf(0xFE76A2, 0xFFFFFF, 0xBF12D7, 0x000000, 0x303CBE),
        "intersex" to colorArrayOf(0xFFD800, 0x7902AA, 0xFFD800),
        "aro" to colorArrayOf(0x3BA740, 0xA8D47A, 0xFFFFFF, 0xABABAB, 0x000000),

        // Colours taken from https://www.hrc.org/resources/lgbtq-pride-flags.
        "baker" to colorArrayOf(0xCD66FF, 0xFF6599, 0xFE0000, 0xFE9900, 0xFFFF01, 0x009900, 0x0099CB, 0x350099, 0x990099),
        "philly" to colorArrayOf(0x000000, 0x784F17, 0xFE0000, 0xFD8C00, 0xFFE500, 0x119F0B, 0x0644B3, 0xC22EDC),
        "queer" to colorArrayOf(0x000000, 0x9AD9EA, 0x00A3E8, 0xB5E51D, 0xFFFFFF, 0xFFC90D, 0xFC6667, 0xFEAEC9, 0x00000),
        "gay" to colorArrayOf(0x078E70, 0x26CEAA, 0x98E8C1, 0xFFFFFF, 0x7BADE2, 0x5049CB, 0x3D1A78),
        "bigender" to colorArrayOf(0xC479A0, 0xECA6CB, 0xD5C7E8, 0xFFFFFF, 0xD5C7E8, 0x9AC7E8, 0x6C83CF),
        "demigender" to colorArrayOf(0x7F7F7F, 0xC3C3C3, 0xFBFF74, 0xFFFFFF, 0xFBFF74, 0xC3C3C3, 0x7F7F7F))

    private fun colorArrayOf(vararg colors: Int): Array<Color> {
        return colors.map { it -> Color(it) }.toTypedArray()
    }

    override fun apply(styling: TextStyling, tag: TagContext) {
        val color = colorMap[tag.last()]

        if(color != null) {
            styling.color = GradientTagResolver.GradientColorElement(color, 0.0)
        }
    }

    override fun isValid(tag: TagContext): Boolean {
        return tag.first() == "pride"
    }
}