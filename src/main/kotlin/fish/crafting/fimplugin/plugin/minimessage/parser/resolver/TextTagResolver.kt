package fish.crafting.fimplugin.plugin.minimessage.parser.resolver

import fish.crafting.fimplugin.plugin.minimessage.parser.TagContext
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling

abstract class TextTagResolver : TagResolver() {
    abstract fun getText(tag: TagContext): String?
    open fun isAlsoStyling() = false
}