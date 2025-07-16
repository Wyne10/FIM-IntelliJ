package fish.crafting.fimplugin.plugin.util

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.DumbUtil
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiManager

object EditorUtil {

    fun refreshGutters(): Boolean {
        val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return true
        val fileEditorManager = FileEditorManager.getInstance(project)
        val psiFile = fileEditorManager.selectedEditor?.file
            ?.let { PsiManager.getInstance(project).findFile(it) } ?: return true
        if(DumbService.isDumb(project)) return false

        DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
        return true
    }

}