package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling

object BoldTagResolver : DecorationTagResolver("bold", "b") {
    override fun apply(
        styling: TextStyling,
        tag: TagContext,
        invert: Boolean
    ) {
        styling.bold = !invert
    }

}