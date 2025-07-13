package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TagStack
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling

abstract class TagResolver(val stack: TagStack = TagStack()) {

    abstract fun apply(styling: TextStyling, tag: TagContext)
    abstract fun isValid(tag: TagContext): Boolean

}