package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiExpressionList
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethodCallExpression
import fish.crafting.fimplugin.plugin.minimessage.parser.MiniMessageParser
import fish.crafting.fimplugin.plugin.minimessage.parser.TextComponent
import fish.crafting.fimplugin.plugin.minimessage.parser.TextStyling
import io.ktor.util.reflect.instanceOf
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints

class MiniMessageRenderer(private val components: ArrayList<TextComponent>,
                          private var text: String,
                          private var width: Int = 1): EditorCustomElementRenderer {

    constructor(text: String) : this(MiniMessageParser.parse(text), text)

    fun updateText(newText: String){
        if(text == newText) return

        components.clear()
        components.addAll(MiniMessageParser.parse(newText))
    }

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        return width
    }

    override fun paint(inlay: Inlay<*>, g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes) {
        val g2 = g as Graphics2D
        val editor = inlay.editor

        var width = 0;
        var x = targetRegion.x
        val y = targetRegion.y + editor.ascent

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF)

        for (component in components) {
            val styledFont = if(component.styling.bold){
                applyStylingToFont(MinecraftFont.bold, component.styling)
            }else{
                applyStylingToFont(MinecraftFont.font, component.styling)
            }

            g2.font = styledFont

            drawText(g2, x + 2, y + 2, component.content, component.styling.getShadow(), component.styling)
            val w = drawText(g2, x, y, component.content, component.styling.color, component.styling)
            x += w
            width += w
        }

        val updateWidth = this.width != width
        this.width = width
        if(updateWidth) inlay.update()
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