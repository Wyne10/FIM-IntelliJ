package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling

object StrikethroughTagResolver : DecorationTagResolver("strikethrough", "st") {

    override fun apply(
        styling: TextStyling,
        tag: TagContext,
        invert: Boolean
    ) {
        styling.strikethrough = !invert
    }

}