package com.klask.jetty

import com.klask.Klask
import org.eclipse.jetty.util.component.LifeCycle

class KlaskServerListener(val app: Klask, val serverReadyListeners: List<(Klask) -> Unit>) : LifeCycle.Listener {
    override fun lifeCycleStarting(event: LifeCycle?) {
    }

    override fun lifeCycleStarted(event: LifeCycle?) {
        serverReadyListeners.forEach { it(app) }
    }

    override fun lifeCycleFailure(event: LifeCycle?, cause: Throwable?) {
    }

    override fun lifeCycleStopping(event: LifeCycle?) {
    }

    override fun lifeCycleStopped(event: LifeCycle?) {
    }

}
