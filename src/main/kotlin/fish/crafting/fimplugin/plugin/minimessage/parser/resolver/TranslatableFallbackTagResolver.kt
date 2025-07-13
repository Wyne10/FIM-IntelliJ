package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import java.awt.Color

object TranslatableFallbackTagResolver : TextTagResolver() {
    private val tags = hashSetOf("translate_or", "lang_or", "tr_or")

    override fun apply(styling: TextStyling, tag: TagContext) {

    }

    override fun isValid(tag: TagContext): Boolean {
        return tag.size >= 3 && tag.first() in tags
    }

    override fun getText(tag: TagContext): String? {
        return tag.slices[2]
    }

    override fun useLowercaseForApply() = false

}