package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

object SelectorTagResolver : TagResolver() {
    private val tags = hashSetOf("selector", "sel")

    override fun apply(styling: TextStyling, tag: TagContext) {
    }

    override fun isValid(tag: TagContext): Boolean {
        return tag.first() in tags
    }

}