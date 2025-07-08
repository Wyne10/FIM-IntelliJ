package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

object PrideTagResolver : TagResolver() {

    override fun apply(styling: TextStyling, tag: TagContext) {
        //TODO when gradients are done
    }

    override fun isValid(tag: TagContext): Boolean {
        return tag.first() == "pride"
    }

}