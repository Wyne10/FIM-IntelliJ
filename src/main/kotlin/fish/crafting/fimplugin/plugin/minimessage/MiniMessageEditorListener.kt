package fish.crafting.fimplugin.plugin.minimessage

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.concurrency.AppExecutorUtil
import fish.crafting.fimplugin.plugin.listener.PluginDisposable
import fish.crafting.fimplugin.plugin.util.DataKeys
import fish.crafting.fimplugin.plugin.util.DataKeys.MM_LISTENERS_REGISTERED

class MiniMessageEditorListener : EditorFactoryListener {

    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        if(editor.project == null || editor.isDisposed) return

        editor.putUserData(DataKeys.INLAY_CONTROLLER, MiniMessageInlayController())
        registerListeners(editor)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        val editor = event.editor
        editor.getUserData(DataKeys.INLAY_CONTROLLER)?.dispose()
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
            /*override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                val controller = editor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return

                controller.updateAllInlines(editor)
            }*/

            override fun selectionChanged(event: FileEditorManagerEvent) {
                val oldController = event.oldEditor?.getUserData(DataKeys.INLAY_CONTROLLER) ?: return
                val newController = event.newEditor?.getUserData(DataKeys.INLAY_CONTROLLER) ?: return

                //controller.removeBlockInlay(editor, false)

                //Update opened file
                /*PsiDocumentManager.getInstance(project).performWhenAllCommitted {
                    controller.handleCaretChanged(editor)
                }*/
                newController.handleCaretChanged(editor)
            }
        })

        /*PsiManager.getInstance(project).addPsiTreeChangeListener(object : PsiTreeChangeListener {
            override fun beforeChildAddition(event: PsiTreeChangeEvent) {

            }

            override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
            }

            override fun beforeChildReplacement(event: PsiTreeChangeEvent) {
            }

            override fun beforeChildMovement(event: PsiTreeChangeEvent) {
            }

            override fun beforeChildrenChange(event: PsiTreeChangeEvent) {
            }

            override fun beforePropertyChange(event: PsiTreeChangeEvent) {
            }

            override fun childAdded(event: PsiTreeChangeEvent) {
                updateChild(event)
            }

            override fun childRemoved(event: PsiTreeChangeEvent) {
            }

            override fun childReplaced(event: PsiTreeChangeEvent) {
                updateChild(event)
            }

            override fun childrenChanged(event: PsiTreeChangeEvent) {
                updateChild(event)
            }

            override fun childMoved(event: PsiTreeChangeEvent) {
            }

            override fun propertyChanged(event: PsiTreeChangeEvent) {
            }


            private fun updateChild(event: PsiTreeChangeEvent){
                val psiElement = event.child ?: return
                if (!psiElement.isValid || psiElement.project.isDisposed) return

                val controller = editor.getUserData(DataKeys.INLAY_CONTROLLER) ?: return

                event.child?.accept(object : JavaRecursiveElementVisitor() {
                    override fun visitLiteralExpression(expression: PsiLiteralExpression) {
                        controller.updateInlineFor(editor, expression)
                    }
                })
            }

        }, PluginDisposable.getInstance())*/
    }
}