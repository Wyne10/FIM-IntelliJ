package fish.crafting.fimplugin.plugin.listener

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.WindowManager
import fish.crafting.fimplugin.connection.packets.I2FIntelliJFocusedPacket
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

@Service(Service.Level.PROJECT)
class FocusDisposable(project: Project): Disposable {

    companion object {
        fun getInstance(project: Project): FocusDisposable? {
            return project.service<FocusDisposable>()
        }
    }

    init {
        Disposer.register(PluginDisposable.getInstance(), this)

        val focusListener = object : WindowFocusListener {
            override fun windowGainedFocus(e: WindowEvent?) {
                I2FIntelliJFocusedPacket().sendToAll()
            }

            override fun windowLostFocus(e: WindowEvent?) {

            }
        }


        val frame = WindowManager.getInstance().getFrame(project)
        frame?.addWindowFocusListener(focusListener)
    }

    override fun dispose() {
    }
}