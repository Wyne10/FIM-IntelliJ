package fish.crafting.fimplugin.connection.tool

import com.intellij.psi.PsiElement

/**
 * Used to store values which are being edited in-game
 * This will be removed later on, when systems for syncing values between IJ-MC are made.
 */
object ValueEditManager {

    var psiElement: PsiElement? = null

}