package fish.crafting.fimplugin.plugin.listener

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import fish.crafting.fimplugin.connection.netty.ConnectionManager
import fish.crafting.fimplugin.plugin.minimessage.MiniMessageInlayController

@Service
class PluginDisposable: Disposable {

    companion object {
        fun getInstance(): Disposable {
            return ApplicationManager.getApplication().getService(PluginDisposable::class.java)
        }
    }

    override fun dispose() {
        ConnectionManager.shutdown()
        MiniMessageInlayController.removeAllFolds(true)
    }
}