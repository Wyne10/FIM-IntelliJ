package fish.crafting.fimplugin.connection.focuser

import com.intellij.ui.mac.foundation.Foundation

class MacProgramFocuser(val pid: Int) : ProgramFocuser {
    override fun focus(): Boolean {
        val nsRunningApplicationClass = Foundation.getObjcClass("NSRunningApplication")
        val nsApplication = Foundation.invoke(nsRunningApplicationClass, "runningApplicationWithProcessIdentifier:", pid)
        return Foundation.invoke(nsApplication, "activateWithOptions:", 1).booleanValue()
    }
}