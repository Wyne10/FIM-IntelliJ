package fish.crafting.fimplugin.plugin.listener

import com.google.protobuf.TextFormat
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import fish.crafting.fimplugin.connection.netty.ConnectionManager
import fish.crafting.fimplugin.connection.packetsystem.PacketManager
import fish.crafting.fimplugin.plugin.minimessage.MinecraftFont
import fish.crafting.fimplugin.plugin.minimessage.TextFormatRegistryService

class ProjectOpenListener : ProjectActivity {
    //Theoretically, it would make more sense to run this on application startup
    //as this initializes the backend netty server needed for communication
    //In practice, this achieves the same thing, as you won't be communicating
    //if no project is open
    override suspend fun execute(project: Project) {
        val font = MinecraftFont.font
        val bold = MinecraftFont.bold
        ConnectionManager.startServer()
        PluginDisposable.getInstance() //Load disposable (server shutdown task) if not loaded already
        TimerService.getInstance()
        FocusDisposable.getInstance(project)
        PacketManager //Just to make sure it initializes, although it's not necessary

        project.messageBus.connect().subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            FileChangedListener
        )

        TextFormatRegistryService.instance.handleProjectOpen(project)
    }
}