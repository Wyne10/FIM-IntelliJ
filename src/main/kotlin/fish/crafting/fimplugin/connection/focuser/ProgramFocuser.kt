package fish.crafting.fimplugin.connection.focuser

import fish.crafting.fimplugin.connection.netty.MinecraftAudience
import fish.crafting.fimplugin.connection.netty.MinecraftManager
import fish.crafting.fimplugin.connection.packets.I2FFocusPacket
import java.awt.event.InputEvent

interface ProgramFocuser {

    companion object {
        fun focusIfSettingCondition(inputEvent: InputEvent?){
            if(inputEvent != null){
                //TODO add a setting for this
                if(inputEvent.isAltDown) return
            }

            focusLatest()
        }

        fun focusLatest() {
            val latestInstance = MinecraftManager.getLatestInstance() ?: return

            if(latestInstance.focus()){
                I2FFocusPacket().send(latestInstance)
            }
        }
    }

    fun focus(): Boolean

}