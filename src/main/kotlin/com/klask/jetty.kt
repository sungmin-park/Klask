package com.klask.jetty

import com.klask.Klask
import com.klask.RequestMethod
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.util.component.LifeCycle
import java.net.HttpURLConnection
import java.net.URL

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

public class JettyServer(val port: Int) : Server(port) {
    public fun get(url: String): String {
        return URL("http://localhost:$port$url")
                .openConnection()
                .getInputStream().use { it.reader().readText() }
    }

    public fun post(url: String): String {
        return URL("http://localhost:$port$url")
                .openConnection().let { it as HttpURLConnection }
                .let {
                    it.setRequestMethod(RequestMethod.POST.toString())
                    it
                }
                .getInputStream().use { it.reader().readText() }
    }
}
