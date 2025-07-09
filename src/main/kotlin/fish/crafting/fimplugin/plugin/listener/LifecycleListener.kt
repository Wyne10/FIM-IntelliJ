package fish.crafting.fimplugin.plugin.listener

import com.intellij.ide.AppLifecycleListener
import fish.crafting.fimplugin.plugin.minimessage.MiniMessageInlayController

class LifecycleListener : AppLifecycleListener {
    override fun appClosing() {
        MiniMessageInlayController.removeAllFolds()
    }
}