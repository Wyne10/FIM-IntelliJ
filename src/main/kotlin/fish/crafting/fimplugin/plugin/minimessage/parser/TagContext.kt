package fish.crafting.fimplugin.plugin.minimessage.parser

import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.TagResolver
import org.jsoup.internal.StringUtil
import kotlin.math.min

class TagContext(val slices: List<String>, val index: Int) {

    var resolver: TagResolver? = null

    //Gradients break if a structure like this is used
    //<gradient> ABC <bold> DEF
    //because each element [ABC, DEF] is treated separately, and gradients were rendered separately.
    //Instead, we track the rendering in the ColorElement, and we need all elements [ABC, DEF] to have the same object
    var cachedColor: TextStyling.LengthTrackingColorElement? = null

    constructor(tag: String, index: Int) : this(tag.split(':'), index)

    fun last(): String = slices.last()
    fun first(): String = slices.first()
    fun hasMoreThanOne() = slices.size > 1
    val size get() = slices.size

    fun isInvert(): Boolean {
        return if(hasMoreThanOne()) last() == "false"
        else first().firstOrNull() == '!'
    }

    /**
     * Match if this TagContext is greater than the provided tag.
     * e.g:
     * this -> color:red
     * tag -> color
     *
     * Will return true, because this matches tag and is greater or equal.
     * Used for bracket closing
     */
    fun matchIfGreaterThan(tag: TagContext): Boolean {
        //If this has less slices
        if(size < tag.size) return false
        val min = min(size, tag.size)

        for (i in 0 until min) {
            if(slices[i] != tag.slices[i]) return false
        }

        return true
    }

    override fun equals(other: Any?): Boolean {
        if(other is String) return other == first()
        return super.equals(other)
    }

    override fun toString(): String {
        return StringUtil.join(slices, ":")
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + slices.hashCode()
        result = 31 * result + (cachedColor?.hashCode() ?: 0)
        result = 31 * result + size
        return result
    }


}