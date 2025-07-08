package fish.crafting.fimplugin.plugin.minimessage.parser

import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.TagResolver

class TagStack(val tags: ArrayDeque<TagContext> = ArrayDeque()) {

    fun removeStartingTop(tag: TagContext): Boolean {
        val iterator = tags.listIterator(tags.size)
        while(iterator.hasPrevious()){
            val previous = iterator.previous()

            if(previous.matchIfGreaterThan(tag)) {
                iterator.remove()

                //Remove all above
                TagResolvers.removeAllHigherThan(previous.index)

                return true
            }
        }

        return false
    }

    //<blue> <bold> </blue>    ....   <red>
    //</blue> should remove <bold>
    //Because this is procedural, <red> has not been indexed yet and will not be affected.
    fun removeAnythingHigherThan(index: Int){
        tags.removeIf { it.index > index }
    }

    fun style(resolver: TagResolver, styling: TextStyling) {
        if(tags.isEmpty()) return
        for (context in tags) {
            resolver.apply(styling, context)
        }
    }

    fun addToTop(context: TagContext) {
        tags.addLast(context)
    }

    fun flush() {
        tags.clear()
    }

}