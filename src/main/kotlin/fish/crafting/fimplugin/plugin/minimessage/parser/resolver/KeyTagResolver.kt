package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

object KeyTagResolver : TextTagResolver() {

    override fun apply(styling: TextStyling, tag: TagContext) {
    }

    override fun isValid(tag: TagContext): Boolean {
        return tag.size > 1 && tag.first() == "key"
    }

    override fun getText(tag: TagContext): String? {
        return tag.last() //Eventually this will work maybe
    }

    override fun useLowercaseForApply() = false

}