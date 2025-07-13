package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

/**
 * Sadly, this one is probably not possible to implement well
 */
object NewlineTagResolver : TagResolver() {

    private val tags = hashSetOf("newline", "br")

    override fun apply(styling: TextStyling, tag: TagContext) {
    }

    override fun isValid(tag: TagContext): Boolean {
        return tag.first() in tags
    }

}