package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling

object ItalicTagResolver : DecorationTagResolver("italic", "i", "em") {

    override fun apply(
        styling: TextStyling,
        tag: TagContext,
        invert: Boolean
    ) {
        styling.italic = !invert
    }

}