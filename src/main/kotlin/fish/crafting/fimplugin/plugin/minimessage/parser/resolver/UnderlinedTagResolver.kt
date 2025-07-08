package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling

object UnderlinedTagResolver : DecorationTagResolver("underlined", "u") {

    override fun apply(
        styling: TextStyling,
        tag: TagContext,
        invert: Boolean
    ) {
        styling.underlined = !invert
    }

}