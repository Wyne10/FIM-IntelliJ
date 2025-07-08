package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

object ClickTagResolver : TagResolver() {

    override fun apply(styling: TextStyling, tag: TagContext) {
    }

    override fun isValid(tag: TagContext): Boolean {
        val first = tag.first()
        return first == "click"
    }

}