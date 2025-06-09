package fish.crafting.fimplugin.plugin.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNewExpression
import fish.crafting.fimplugin.connection.focuser.ProgramFocuser
import fish.crafting.fimplugin.connection.netty.MinecraftAudience
import fish.crafting.fimplugin.connection.packets.I2FTeleportPacket
import fish.crafting.fimplugin.plugin.util.mc.Location
import fish.crafting.fimplugin.plugin.util.mc.Vector
import org.jetbrains.kotlin.psi.KtCallExpression
import java.awt.event.InputEvent

object ActionUtils {

    fun tpVector(element: PsiElement, event: InputEvent?) {
        val vector = ConversionUtil.elementToVector(element) ?: return

        ProgramFocuser.focusIfSettingCondition(event)
        I2FTeleportPacket(vector).send(MinecraftAudience.LATEST)
        //println("Teleporting to $vector")
    }

    fun tpLocation(element: PsiElement, event: InputEvent?) {
        val vector = ConversionUtil.elementToLocation(element) ?: return

        ProgramFocuser.focusIfSettingCondition(event)
        I2FTeleportPacket(vector).send(MinecraftAudience.LATEST)
        //println("Teleporting to $vector")
    }

}

fun PsiNewExpression.ifVector(thenRun: (Vector) -> Unit) {
    val vector = ConversionUtil.elementToVector(this) ?: return
    thenRun.invoke(vector)
}

fun PsiNewExpression.ifLocation(thenRun: (Location) -> Unit) {
    val loc = ConversionUtil.elementToLocation(this) ?: return
    thenRun.invoke(loc)
}

fun KtCallExpression.ifVector(thenRun: (Vector) -> Unit) {
    val vector = ConversionUtil.elementToVector(this) ?: return
    thenRun.invoke(vector)
}

fun KtCallExpression.ifLocation(thenRun: (Location) -> Unit) {
    val loc = ConversionUtil.elementToLocation(this) ?: return
    thenRun.invoke(loc)
}