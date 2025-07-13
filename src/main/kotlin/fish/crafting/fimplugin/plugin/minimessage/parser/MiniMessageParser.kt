package fish.crafting.fimplugin.plugin.minimessage.parser

import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.TagResolver
import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.TextTagResolver
import java.util.concurrent.atomic.AtomicBoolean

object MiniMessageParser {
    fun parseOrLegacy(text: String, hadAnyFormat: AtomicBoolean? = null): ArrayList<TextComponent> {
        if(text.contains('<') && text.contains('>')) {
            val hadTags = AtomicBoolean(false)
            val parsed = parseMiniMessage(text, hadTags)
            if(hadTags.get()) {
                hadAnyFormat?.set(true)
                return parsed
            }
        }

        return LegacyFormatParser.parse(text, hadTags = hadAnyFormat)
    }

    fun parseMiniMessage(message: String, hadTags: AtomicBoolean? = null): ArrayList<TextComponent> {
        TagResolvers.flushAll()

        var text = message
        val stringBuilder by lazy { StringBuilder(text) }
        val output = arrayListOf<TextComponent>()
        var activeStyling: TextStyling = TextStyling.default

        var i = 0
        var inQuotes = false
        var openIndex = -1
        var textStart = -1

        fun addOutput(subText: String, styling: TextStyling){
            output.add(TextComponent(subText, styling))

            val color = activeStyling.color
            if(color is TextStyling.LengthTrackingColorElement){
                color.textLength += subText.length
            }
        }

        while(i < text.length) {
            val ch = text[i]

            //We only want to consider quotes that are inside tags, to avoid situations where text that contains > is in tags.
            if((ch == '"' || ch == '\'') && openIndex != -1){
                inQuotes = !inQuotes
            }

            if(inQuotes){
                i++
                continue
            }

            if(ch == '<'){
                openIndex = i
            }else if(ch == '>' && openIndex != -1){
                val originalTagStr = text.substring(openIndex + 1, i)
                var tagStr = originalTagStr.lowercase()

                val closing = tagStr.startsWith("/")
                if(closing && tagStr.length > 1) tagStr = tagStr.substring(1)

                val context = TagContext(tagStr, i)
                val tag = TagResolvers.getResolver(context)

                //If tag == null, then tag is invalid, continue as normal, use this in text.
                //If the tag was resolved correctly, add the previous text in.
                var success = false
                if(tag != null){
                    if(!tag.useLowercaseForApply()){
                        context.updateSlices(originalTagStr)
                    }

                    context.resolver = tag
                    if(tag is TextTagResolver){
                        if(!closing){ //text tags aren't closable but should be parsed
                            tag.getText(context)?.let{
                                stringBuilder.insert(i + 1, it)
                                text = stringBuilder.toString()
                                success = true

                                if(textStart != -1){ //Had text
                                    addOutput(text.substring(textStart, openIndex), activeStyling)
                                    textStart = -1
                                }
                            }
                        }
                    }

                    if(tag !is TextTagResolver || tag.isAlsoStyling()){
                        //Now, for closing tags, they may not always work, even if they were matched to a resolver.
                        //For example, </blue> is valid but won't be parsed if there wasn't a <blue> before.
                        val newStyling = applyStyling(tag, context, closing)
                        if(newStyling != null){
                            //Okay we passed all checks

                            hadTags?.set(true)

                            if(textStart != -1){ //Had text
                                addOutput(text.substring(textStart, openIndex), activeStyling)
                                textStart = -1
                            }

                            activeStyling = newStyling
                            success = true
                        }
                    }
                }

                if(!success && textStart == -1){ //Tag wasn't parsed successfully, but we don't have any text yet. Mark this as the start of text then
                    textStart = openIndex
                }


                openIndex = -1 //Mark as not currently in brackets
            }else if(openIndex == -1 && textStart == -1){ //Not inside bracket, and no text present yet
                textStart = i
            }

            i++
        }

        if(textStart != -1){ //Had text
            addOutput(text.substring(textStart), activeStyling)
        }

        TagResolvers.flushAll()
        return output
    }

    private fun resetAllTags(){
        TagResolvers.forEach {
            it.stack.flush()
        }
    }

    private fun applyStyling(tag: TagResolver, context: TagContext, closing: Boolean): TextStyling? {
        if(tag == TagResolvers.RESET){
            if(!closing) resetAllTags()
            return TextStyling.default //Reset to default
        }

        if(closing){
            val successfullyRemoved = tag.stack.removeStartingTop(context)
            if(!successfullyRemoved) return null
        }else{
            tag.stack.addToTop(context)
        }

        val styling = TextStyling.default
        TagResolvers.style(styling)

        return styling
    }

}