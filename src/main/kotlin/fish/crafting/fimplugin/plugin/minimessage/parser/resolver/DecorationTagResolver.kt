package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling

abstract class DecorationTagResolver(val validTags: Set<String>) : TagResolver() {

    constructor(vararg validTags: String) : this(validTags.toHashSet())

    final override fun apply(styling: TextStyling, tag: TagContext) {
        apply(styling, tag, tag.isInvert())
    }

    abstract fun apply(styling: TextStyling, tag: TagContext, invert: Boolean)

    override fun isValid(tag: TagContext): Boolean {
        var first = tag.first()
        if(first.startsWith("!") && first.length > 1) first = first.substring(1)

        return first in validTags
    }
}