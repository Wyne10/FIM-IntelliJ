package fish.crafting.fimplugin.plugin.listener

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.EdtExecutorService
import fish.crafting.fimplugin.connection.netty.ConnectionManager
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Service
class TimerService: Disposable {

    companion object {
        private const val TICK_DURATION = 1L
        private val TICK_UNIT: TimeUnit = TimeUnit.SECONDS

        fun getInstance(): Disposable {
            return ApplicationManager.getApplication().getService(TimerService::class.java)
        }
    }

    init {
        Disposer.register(PluginDisposable.getInstance(), this)
    }

    private var ticker: ScheduledFuture<*>? = EdtExecutorService
        .getScheduledExecutorInstance()
        .scheduleWithFixedDelay(this::tick, TICK_DURATION, TICK_DURATION, TICK_UNIT);

    private fun tick() { //1 second loop
        synchronized(this) {
            ConnectionManager.getServer()?.tick()
        }
    }

    override fun dispose() {
        ticker?.cancel(false)
        ticker = null
    }
}