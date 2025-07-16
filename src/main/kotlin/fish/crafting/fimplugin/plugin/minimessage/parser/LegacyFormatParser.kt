package fish.crafting.fimplugin.plugin.minimessage.parser

import com.intellij.ui.ColorHexUtil
import fish.crafting.fimplugin.plugin.minimessage.toHexOrNull
import org.jetbrains.jewel.ui.component.Text
import java.awt.Color
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern

object LegacyFormatParser {

    private val validCharacters = "abcdef0123456789klmnor".toCharArray()

    val colorMap = mapOf(
        "0" to Color(0x000000),
        "1" to Color(0x0000AA),
        "2" to Color(0x00AA00),
        "3" to Color(0x00AAAA),
        "4" to Color(0xAA0000),
        "5" to Color(0xAA00AA),
        "6" to Color(0xFFAA00),
        "7" to Color(0xAAAAAA),
        "8" to Color(0x555555),
        "9" to Color(0x5555FF),
        "a" to Color(0x55FF55),
        "b" to Color(0x55FFFF),
        "c" to Color(0xFF5555),
        "d" to Color(0xFF55FF),
        "e" to Color(0xFFFF55),
        "f" to Color(0xFFFFFF)
    )

    //this sucks
    fun parse(text: String, triggerCharacter: Char = '&', hadTags: AtomicBoolean = AtomicBoolean()): ArrayList<TextComponent> {
        if(!text.contains(triggerCharacter)) {
            return arrayListOf(TextComponent(text, TextStyling()))
        }

        val output = arrayListOf<TextComponent>()
        var isAmpersandReached = false
        var isHexReached = false
        var hexString = ""
        var currentStyling = TextStyling()
        var textStart = 0

        var i = 0

        fun resetReached() {
            isAmpersandReached = false
            isHexReached = false
            hexString = ""
        }

        fun apply(symbol: String) {
            var color: Color?
            if(symbol.length > 1) { //hex
                color = symbol.toHexOrNull()
            }else{
                color = colorMap[symbol]
            }

            val oldStyling = currentStyling.clone()

            var valid = false
            if(color != null){ //Is color
                valid = true
                currentStyling.resetDecorators()
                currentStyling.color = TextStyling.SolidColorElement(color)
            }else{ //Is something else
                valid = true
                when(symbol) {
                    "k" -> currentStyling.obfuscated = true
                    "l" -> currentStyling.bold = true
                    "m" -> currentStyling.strikethrough = true
                    "n" -> currentStyling.underlined = true
                    "o" -> currentStyling.italic = true
                    "r" -> currentStyling = TextStyling()
                    else -> valid = false
                }
            }

            if(valid) hadTags.set(true)

            //i is currently at the symbol position
            //e.g. &0
            //so we switch by -symbol.length(), to make it at the &, because substring is exclusive at the end
            val textEnd = i - symbol.length
            if(textEnd > textStart) { //textEnd may == textStart if they are chained, e.g. &b&l
                val substring = text.substring(textStart, textEnd)
                output.add(TextComponent(substring, oldStyling))
            }
            //textStart should be the next letter after &0
            textStart = i + 1
        }

        while(i < text.length) {
            val ch = text[i].lowercaseChar()

            if(ch == triggerCharacter) {
                isAmpersandReached = true
            }else if(ch == '#'){
                if(isAmpersandReached) {
                    isHexReached = true
                    hexString = ""
                }
            }else{ //Not & and not #
                if(isHexReached){
                    hexString += ch
                    if(hexString.length == 6) {
                        val hex = "#$hexString".toHexOrNull()

                        if(hex != null){
                            apply("#$hexString")
                        }

                        resetReached()
                    }
                }else if(isAmpersandReached) {
                    if(ch in validCharacters){
                        apply("$ch")
                    }

                    resetReached()
                }
            }

            i++
        }

        if(textStart < text.length) { //Last text hasn't been parsed
            output.add(TextComponent(text.substring(textStart), currentStyling))
        }

        if(output.isEmpty() && hadTags.get()){
            output.add(TextComponent("", currentStyling))
        }

        return output
    }
}