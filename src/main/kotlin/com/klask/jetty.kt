package com.klask.jetty

import com.klask.Klask
import com.klask.RequestMethod
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.util.component.LifeCycle
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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

    public fun post(url: String, data: List<Pair<String, String>> = listOf()): String {
        return URL("http://localhost:$port$url")
                .openConnection()
                .let { it as HttpURLConnection }
                .let { httpURLConnection ->
                    httpURLConnection.setRequestMethod(RequestMethod.POST.toString())
                    data.map { it.toList().map { URLEncoder.encode(it, "UTF-8") }.join("=") }.join("&").let { queryString ->
                        if (queryString.length() > 0) {
                            httpURLConnection.setDoOutput(true)
                            httpURLConnection.getOutputStream().use { it.writer().use { it.write(queryString) } }
                        }
                    }
                    httpURLConnection
                }
                .getInputStream().use { it.reader().readText() }
    }
}
