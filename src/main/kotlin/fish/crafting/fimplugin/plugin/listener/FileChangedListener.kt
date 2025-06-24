package fish.crafting.fimplugin.plugin.listener

import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import fish.crafting.fimplugin.connection.packets.I2FLangPacket
import org.jetbrains.kotlin.idea.util.isJavaFileType
import org.jetbrains.kotlin.idea.util.isKotlinFileType

object FileChangedListener : FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val newFile = event.newFile ?: return

        if(newFile.isKotlinFileType()){
            I2FLangPacket(true).sendToAll()
        }else if(newFile.isJavaFileType()){
            I2FLangPacket(false).sendToAll()
        }

    }

}