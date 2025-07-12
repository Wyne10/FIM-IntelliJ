package fish.crafting.fimplugin.plugin.minimessage.parser

import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.*
import org.apache.xml.resolver.apps.resolver

object TagResolvers {

    private val allResolvers = arrayListOf<TagResolver>()
    private fun <T : TagResolver> register(resolver: T): TagResolver {
        allResolvers.add(resolver)
        return resolver
    }

    fun getResolver(tag: TagContext): TagResolver? {
        return allResolvers.firstOrNull{ it.isValid(tag) }
    }

    fun flushAll() {
        allResolvers.forEach { it.stack.flush() }
    }

    fun style(styling: TextStyling) {
        allResolvers.forEach { it.stack.style(it, styling) }
    }

    fun removeAllHigherThan(index: Int) {
        allResolvers.forEach { it.stack.removeAnythingHigherThan(index) }
    }

    fun forEach(forEach: (TagResolver) -> Unit) {
        allResolvers.forEach(forEach)
    }

    val COLOR = register(ColorTagResolver)
    val BOLD = register(BoldTagResolver)
    val FONT = register(FontTagResolver)
    val ITALIC = register(ItalicTagResolver)
    val SHADOW = register(ShadowColorTagResolver)
    val UNDERLINE = register(UnderlinedTagResolver)
    val OBFUSCATED = register(ObfuscatedTagResolver)
    val STRIKETHROUGH = register(StrikethroughTagResolver)
    val TRANSLATE = register(TranslatableFallbackTagResolver)
    val TRANSLATE_FALLBACK = register(TranslatableFallbackTagResolver)
    val CLICK = register(ClickTagResolver)
    val INSERTION = register(InsertionTagResolver)
    val NBT = register(NBTTagResolver)
    val PRIDE = register(PrideTagResolver)
    val SCORE = register(ScoreTagResolver)
    val SELECTOR = register(SelectorTagResolver)
    val GRADIENT = register(GradientTagResolver)
    val RESET = register(ResetTagResolver)

}