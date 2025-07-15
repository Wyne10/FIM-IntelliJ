package fish.crafting.fimplugin.connection.tool

import com.intellij.psi.PsiElement

/**
 * Used to store values which are being edited in-game
 * This will be removed later on, when systems for syncing values between IJ-MC are made.
 */
object ValueEditManager {

    //TODO change this to use cached PsiElements
    //Shouldn't be a problem for now as any MC edits get discarded if you focus IntelliJ
    var psiElement: PsiElement? = null

}