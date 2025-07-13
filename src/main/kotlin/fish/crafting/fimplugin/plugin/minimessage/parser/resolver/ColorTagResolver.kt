package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import com.intellij.ui.ColorHexUtil
import fish.crafting.fimplugin.plugin.minimessage.parser.CommonTagStacks
import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

object ColorTagResolver : TagResolver(CommonTagStacks.COLOR) {

    val colorMap = mapOf(
        "black" to Color(0x000000),
        "dark_blue" to Color(0x0000AA),
        "dark_green" to Color(0x00AA00),
        "dark_aqua" to Color(0x00AAAA),
        "dark_red" to Color(0xAA0000),
        "dark_purple" to Color(0xAA00AA),
        "gold" to Color(0xFFAA00),
        "gray" to Color(0xAAAAAA),
        "grey" to Color(0xAAAAAA),
        "dark_gray" to Color(0x555555),
        "dark_grey" to Color(0x555555),
        "blue" to Color(0x5555FF),
        "green" to Color(0x55FF55),
        "aqua" to Color(0x55FFFF),
        "red" to Color(0xFF5555),
        "light_purple" to Color(0xFF55FF),
        "yellow" to Color(0xFFFF55),
        "white" to Color(0xFFFFFF)
    )

    val verboseTags = hashSetOf("color", "c", "colour")

    override fun apply(styling: TextStyling, tag: TagContext) {
        val color = resolveColor(tag.last())

        if(color != null) {
            styling.color = TextStyling.SolidColorElement(color)
        }
    }

    override fun isValid(tag: TagContext): Boolean {
        if(tag.size == 2 && tag.first() !in verboseTags) return false
        if(tag.size >= 3) return false

        val color = tag.last()
        if(tag.size == 1 && color == "color") return true //</color>

        if(color.startsWith("#")) {
            return ColorHexUtil.fromHexOrNull(color) != null
        }else{
            return colorMap.containsKey(color)
        }
    }

    fun resolveColor(colorStr: String): Color? {
        return if(colorStr.startsWith("#")){
            try{
                ColorHexUtil.fromHexOrNull(colorStr)
            }catch (ignored: Exception) {
                null
            }
        }else{
            colorMap[colorStr]
        }
    }

}