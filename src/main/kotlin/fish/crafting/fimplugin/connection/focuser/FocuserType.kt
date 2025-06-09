package fish.crafting.fimplugin.connection.focuser

import com.intellij.execution.process.ProcessHandler
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import org.apache.commons.lang3.SystemUtils

enum class FocuserType(val focuserID: Int) {

    NONE(0),
    WINDOWS(1),
    LINUX(2),
    MAC(3);

    companion object {

        //to be honest, there probably needs to be changed
        fun getCurrent(): FocuserType {
            return if(SystemUtils.IS_OS_WINDOWS) {
                WINDOWS
            }else if(SystemUtils.IS_OS_MAC) {
                MAC
            }else if(SystemUtils.IS_OS_LINUX) {
                LINUX
            }else{
                NONE
            }
        }

        /*
         *  GUIDE FOR WINDOW FOCUSER
         *
         *  VALUES:
         *
         *  SHORT: ID (Dependent on ID, use the next values)
         *  0 (No Focuser):
         *    NONE
         *  1 (Windows Focuser):
         *    INT: PID
         *  2 (Linux Focuser):
         *    INT: PID
         *  3 (Mac Focuser):
         *    INT: PID
         *
         */

        fun readFromStream(stream: ByteBufInputStream): ProgramFocuser? {
            val currentFocuserID = FocuserType.getCurrent().focuserID
            val focuserID = stream.readShort().toInt()

            return when(focuserID){
                1 -> {
                    val pid = stream.readInt()

                    //If the focuser IDs are mismatched, we need to return null
                    //however, we still need to read all the values to keep everything flowing
                    if(currentFocuserID != 1) null
                    WindowsProgramFocuser(pid)
                }
                2 -> {
                    val pid = stream.readInt()

                    if(currentFocuserID != 2) null
                    X11ProgramFocuser(pid)
                }
                3 -> {
                    val pid = stream.readInt()

                    if(currentFocuserID != 3) null
                    MacProgramFocuser(pid)
                }
                else -> { null }
            }
        }
    }

}