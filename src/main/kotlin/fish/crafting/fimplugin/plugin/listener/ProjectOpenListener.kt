package fish.crafting.fimplugin.plugin.listener

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import fish.crafting.fimplugin.connection.netty.ConnectionManager
import fish.crafting.fimplugin.connection.packetsystem.PacketManager

class ProjectOpenListener : ProjectActivity {
    //Theoretically, it would make more sense to run this on application startup
    //as this initializes the backend netty server needed for communication
    //In practice, this achieves the same thing, as you won't be communicating
    //if no project is open
    override suspend fun execute(project: Project) {
        ConnectionManager.startServer()
        PluginDisposable.getInstance() //Load disposable (server shutdown task) if not loaded already
        TimerService.getInstance()
        FocusDisposable.getInstance(project)
        PacketManager //Just to make sure it initializes, although it's not necessary
    }
}