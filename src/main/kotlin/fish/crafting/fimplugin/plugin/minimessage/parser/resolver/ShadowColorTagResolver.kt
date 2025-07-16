package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import com.intellij.java.compiler.charts.ui.alpha
import com.intellij.ui.ColorHexUtil
import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.ColorTagResolver.colorMap
import fish.crafting.fimplugin.plugin.minimessage.toHexOrNull

object ShadowColorTagResolver : TagResolver() {

    override fun apply(styling: TextStyling, tag: TagContext) {
        val colorStr = tag.slices[1]

        var alpha = 1.0
        if(tag.size == 3){
            val aStr = tag.last()
            val toDoubleOrNull = aStr.toDoubleOrNull()
            if(toDoubleOrNull != null) alpha = toDoubleOrNull;
            else {
                val toFloatOrNull = aStr.toFloatOrNull()
                if(toFloatOrNull != null) alpha = toFloatOrNull.toDouble()
            }
        }

        var color = if(colorStr.startsWith("#")){
            colorStr.toHexOrNull()
        }else{
            colorMap[colorStr]
        }

        if(color != null){
            color = color.alpha(alpha)
        }

        styling.shadowColor = color
    }

    override fun isValid(tag: TagContext): Boolean {
        if(tag.size == 1){
            return tag.first() == "!shadow"
        }else if(tag.first() != "shadow"){
            return false
        }

        val color = tag.slices[1]

        if(color.startsWith("#")) {
            if(color.toHexOrNull() == null) return false
        }else{
            if(!colorMap.containsKey(color)) return false
        }

        if(tag.size == 2) return true
        if(tag.size == 3) {
            val alpha = tag.last()
            return alpha.toDoubleOrNull() != null || alpha.toFloatOrNull() != null
        }

        return false
    }

}