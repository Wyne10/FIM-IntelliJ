package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiDocumentManager
import fish.crafting.fimplugin.plugin.listener.PluginDisposable
import fish.crafting.fimplugin.plugin.util.DataKeys
import fish.crafting.fimplugin.plugin.util.DataKeys.MM_LISTENERS_REGISTERED

class MiniMessageEditorListener : EditorFactoryListener {


    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        if(editor.project == null || editor.isDisposed) return

        val controller = MiniMessageInlayController()
        editor.putUserData(DataKeys.INLAY_CONTROLLER, controller)
        registerListeners(editor)

        //this COULD break shit
        controller.updateAllInlines(editor)
        controller.handleCaretChanged(editor)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        val editor = event.editor
        editor.getUserData(DataKeys.INLAY_CONTROLLER)?.dispose(editor)
    }

    private fun registerListeners(editor: Editor){

        if (editor.getUserData(MM_LISTENERS_REGISTERED) == true) return
        editor.putUserData(MM_LISTENERS_REGISTERED, true)

        val project = editor.project ?: return

        EditorFactory.getInstance().eventMulticaster.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                val controller = editor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return

                PsiDocumentManager.getInstance(project).performWhenAllCommitted {
                    controller.handleDocumentChanged(editor)
                }
            }
        }, PluginDisposable.getInstance())

        editor.caretModel.addCaretListener(object : CaretListener {
            override fun caretPositionChanged(event: CaretEvent) {
                val controller = editor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return

                PsiDocumentManager.getInstance(project).performWhenAllCommitted {
                    controller.handleCaretChanged(editor)
                }
            }
        })

        project.messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
                if(event.oldEditor is TextEditor){
                    val oldEditor = (event.oldEditor as TextEditor).editor
                    val oldController = oldEditor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return
                    oldController.removeFolds(oldEditor)
                }

                onFileOpen(event.newEditor)
            }
        })

        project.messageBus.connect().subscribe(DumbService.DUMB_MODE, object : DumbService.DumbModeListener {
            override fun enteredDumbMode() { //Indexing started
            }

            override fun exitDumbMode() { //Indexing complete, rebuild the shown class
                onFileOpen(FileEditorManager.getInstance(project).focusedEditor)
            }
        })
    }

    private fun onFileOpen(fileEditor: FileEditor?){
        if(fileEditor is TextEditor){
            val newEditor = fileEditor.editor
            val newController = newEditor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return

            newController.updateAllInlines(newEditor)
            newController.handleCaretChanged(newEditor)
        }
    }
}