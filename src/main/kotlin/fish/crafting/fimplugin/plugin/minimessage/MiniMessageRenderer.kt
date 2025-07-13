package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import fish.crafting.fimplugin.plugin.minimessage.parser.MiniMessageParser
import fish.crafting.fimplugin.plugin.minimessage.parser.TextComponent
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.GradientTagResolver
import fish.crafting.fimplugin.plugin.minimessage.parser.resolver.RainbowTagResolver
import fish.crafting.fimplugin.plugin.util.ObfuscationUtil
import java.awt.*

class MiniMessageRenderer(private val components: ArrayList<TextComponent>,
                          private var text: String,
                          private var width: Int = 1,
                          private var renderIndex: Int = 0,
                          private var attachedTimer: Boolean = false): EditorCustomElementRenderer {

    constructor(text: String) : this(MiniMessageParser.parseOrLegacy(text), text)

    private var isObfuscated: Boolean? = null
    fun hasObfuscation(): Boolean {
        if(isObfuscated == null) {
            isObfuscated = false

            for (component in components) {
                if(component.styling.obfuscated) {
                    isObfuscated = true
                    break
                }
            }
        }

        return isObfuscated ?: false
    }

    val hasAttachedTimer get() = attachedTimer
    fun attachTimer() {
        attachedTimer = true
    }

    fun updateText(newText: String): Boolean {
        if(text == newText) return false

        components.clear()
        components.addAll(MiniMessageParser.parseOrLegacy(newText))
        isObfuscated = null //Re-calculate obfuscation
        return true
    }

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        return width
    }

    override fun paint(inlay: Inlay<*>, g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes) {
        renderIndex++

        val g2 = g as Graphics2D
        val editor = inlay.editor

        var width = 0;
        var x = targetRegion.x
        val y = targetRegion.y + editor.ascent

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF)

        for (component in components) {
            val baseFont = if (component.styling.bold) {
                MinecraftFont.bold
            } else {
                MinecraftFont.font
            }

            var text = component.content

            if (component.styling.obfuscated) {
                val metrics = g.getFontMetrics(baseFont)
                text = ObfuscationUtil.obfuscate(g, metrics, baseFont, text)
            }

            val styledFont = applyStylingToFont(baseFont, component.styling)
            g2.font = styledFont

            // Drawing Text

            val baseColor = component.styling.color

            val addedWidth = when(baseColor) {
                is GradientTagResolver.GradientColorElement -> {
                    renderIndividuallyColoredChars(g2, text, x, y, component, baseColor::getColor)
                }
                is RainbowTagResolver.RainbowColorElement -> {
                    renderIndividuallyColoredChars(g2, text, x, y, component, baseColor::getColor)
                }
                is TextStyling.SolidColorElement -> {
                    renderSolidColor(g2, text, baseColor, x, y, component)
                }
                else -> 0
            }

            x += addedWidth
            width += addedWidth

        }

        val updateWidth = this.width != width
        this.width = width
        if(updateWidth) inlay.update()
    }

    //Yes I know this is terrible
    private fun renderIndividuallyColoredChars(g2: Graphics2D,
                                               text: String,
                                               x: Int, y: Int,
                                               component: TextComponent,
                                               colorGetter: (Int, Int) -> Color): Int{
        var width = 0
        var x2 = x
        for (ch in text) {
            val letter = ch.toString()
            val color = colorGetter.invoke(1, renderIndex)

            drawText(g2, x2 + 2, y + 2, letter, component.styling.getShadow(color), component.styling)
            val w = drawText(g2, x2, y, letter, color, component.styling)

            x2 += w
            width += w
        }

        return width
    }

    private fun renderSolidColor(g2: Graphics2D,
                                 text: String,
                                 colorElement: TextStyling.SolidColorElement,
                                 x: Int, y: Int,
                                 component: TextComponent): Int {
        val color = colorElement.color
        drawText(g2, x + 2, y + 2, text, component.styling.getShadow(color), component.styling)
        return drawText(g2, x, y, text, color, component.styling)
    }

    private fun drawText(g2: Graphics2D, x: Int, y: Int, text: String, color: Color, styling: TextStyling): Int {
        g2.color = color
        val width = g2.fontMetrics.stringWidth(text)
        g2.drawString(text, x, y)

        if (styling.underlined) {
            val underlineY = y + 1
            g2.drawLine(x, underlineY, x + width, underlineY)
        }

        if (styling.strikethrough) {
            val strikeY = y - g2.fontMetrics.height / 3
            g2.drawLine(x, strikeY, x + width, strikeY)
        }

        return width
    }

    private fun applyStylingToFont(base: Font, style: TextStyling): Font {
        var styleFlags = Font.PLAIN
        if (style.bold) styleFlags = styleFlags or Font.BOLD
        if (style.italic) styleFlags = styleFlags or Font.ITALIC
        return base.deriveFont(styleFlags)
    }
}