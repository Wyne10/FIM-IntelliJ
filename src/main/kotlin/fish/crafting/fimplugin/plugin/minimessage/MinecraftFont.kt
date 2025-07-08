package fish.crafting.fimplugin.plugin.minimessage

import java.awt.Font

object MinecraftFont {
    val font: Font by lazy {
        val stream = MinecraftFont::class.java.classLoader.getResourceAsStream("assets/fonts/Minecraft.ttf")
            ?: error("Could not load Minecraft font")

        Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16f)
    }

    val bold: Font by lazy {
        val stream = MinecraftFont::class.java.classLoader.getResourceAsStream("assets/fonts/Minecraft-Bold.ttf")
            ?: error("Could not load Minecraft Bold font")

        Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16f)
    }
}