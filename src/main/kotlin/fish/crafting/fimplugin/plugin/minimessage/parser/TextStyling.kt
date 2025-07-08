package fish.crafting.fimplugin.plugin.minimessage.parser

import java.awt.Color

class TextStyling(var color: Color = Color.WHITE,
                  var shadowColor: Color? = null,
                  var bold: Boolean = false,
                  var italic: Boolean = false,
                  var underlined: Boolean = false,
                  var strikethrough: Boolean = false,
                  var obfuscated: Boolean = false) {

    companion object{
        val default get() = TextStyling()
    }

    fun getShadow(): Color {
        return shadowColor ?: Color(color.red shr 2, color.green shr 2, color.blue shr 2)
    }

    fun clone() = TextStyling(color, shadowColor, bold, italic, underlined, strikethrough, obfuscated)
}