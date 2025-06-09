package fish.crafting.fimplugin.connection.focuser

import com.intellij.openapi.components.service
import com.intellij.openapi.wm.impl.X11UiUtil
import com.intellij.ui.mac.foundation.Foundation

//Copied from IntelliJ's MacBringProcessWindowToForegroundSupport
class MacProgramFocuser(val pid: Int) : ProgramFocuser {
    override fun focus(): Boolean {
        val nsRunningApplicationClass = Foundation.getObjcClass("NSRunningApplication")
        val nsApplication = Foundation.invoke(nsRunningApplicationClass, "runningApplicationWithProcessIdentifier:", pid)
        return Foundation.invoke(nsApplication, "activateWithOptions:", 1).booleanValue()
    }
}