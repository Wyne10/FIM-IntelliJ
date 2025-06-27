package fish.crafting.fimplugin.plugin.assets

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object FIMAssets {

    val VECTOR = icon("actions/vector.png")
    val LOCATION = icon("actions/location.png")
    val MINIMESSAGE = icon("actions/minimessage.png")
    val BOUNDING_BOX = icon("actions/boundingbox.png")

    private fun icon(file: String) = load("/assets/icons/$file")

    private fun load(file: String): Icon {
        return IconLoader.getIcon(file, FIMAssets::class.java)
    }

}