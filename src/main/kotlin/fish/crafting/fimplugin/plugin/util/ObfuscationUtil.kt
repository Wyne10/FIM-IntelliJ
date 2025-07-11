package fish.crafting.fimplugin.plugin.util

import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.util.Arrays
import kotlin.random.Random


object ObfuscationUtil {

    private const val MIN_ASCII = 32
    private const val MAX_ASCII = 126

    private val obfFontMap = hashMapOf<Font, ObfuscatedFont>()

    fun getObfuscatedFont(graphics: Graphics2D, font: Font): ObfuscatedFont? {
        return obfFontMap.computeIfAbsent(font) { k -> createObfuscatedFont(graphics, font) }
    }

    private fun createObfuscatedFont(graphics: Graphics2D, font: Font): ObfuscatedFont {
        val obfFont = ObfuscatedFont()
        val metrics = graphics.getFontMetrics(font)
        val tempMap = hashMapOf<Int, ArrayList<Char>>()

        for (c in MIN_ASCII..MAX_ASCII) {
            val width: Int = metrics.charWidth(c)
            tempMap.computeIfAbsent(width, { k -> arrayListOf() }).add(c.toChar())
        }

        tempMap.forEach { entry -> obfFont.widthMap.put(entry.key, entry.value.toCharArray()) }
        return obfFont
    }

    fun removeObfuscatedFont(font: Font) {
        val remove = obfFontMap.remove(font)
        remove?.dispose()
    }

    fun obfuscate(graphics: Graphics2D, fontMetrics: FontMetrics, font: Font, text: String, random: Random = Random(System.nanoTime())): String {
        val obfFont = getObfuscatedFont(graphics, font) ?: return text

        val obf = StringBuilder()
        for (ch in text) {
            val width = obfFont.getWidth(fontMetrics, ch)
            if(width == null) {
                obf.append(ch)
                continue
            }

            val obfChar = obfFont.getRandom(width, random) ?: ch
            obf.append(obfChar)
        }

        return obf.toString()
    }

    class ObfuscatedFont(val widthMap: HashMap<Int, CharArray> = hashMapOf(),
                         val charToWidth: Array<Int?> = arrayOfNulls(MAX_ASCII - MIN_ASCII + 1)
    ) {
        fun getRandom(width: Int, random: Random): Char? {
            val chars = widthMap[width] ?: return null
            return chars.random(random)
        }

        fun dispose() {
            widthMap.clear()
        }

        fun getWidth(metrics: FontMetrics, ch: Char): Int? {
            val ascii = ch.code

            if(ascii < MIN_ASCII || ascii > MAX_ASCII) return null
            val index = ascii - MIN_ASCII

            val cachedWidth = charToWidth[index]
            if(cachedWidth != null) {
                return if(cachedWidth <= 0){
                    null
                } else {
                    cachedWidth
                }
            }

            val width = metrics.charWidth(ch)
            charToWidth[index] = width
            return width
        }

    }

}