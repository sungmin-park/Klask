package com.klask

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import javax.servlet.http.HttpServlet
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.util.component.LifeCycle
import kotlin.properties.Delegates
import sun.reflect.generics.reflectiveObjects.NotImplementedException

class Response

Retention(RetentionPolicy.RUNTIME)
annotation class Route(val rule: String)

open class Blueprint

enum class RequestMethod {GET POST }

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

open class Klask : HttpServlet() {
    val router = Router(this)
    var server: Server by Delegates.notNull()

    private val serverReadyListeners = arrayListOf<(app: Klask) -> Unit>()
    private val serverListener = KlaskServerListener(this, serverReadyListeners);

    public fun run(port: Int = 8080, onBackground: Boolean = false) {
        server = Server(port)
        val handler = ServletHandler()
        handler.addServletWithMapping(ServletHolder(this), "/*")
        server.setHandler(handler)
        server.addLifeCycleListener(serverListener)
        server.start()
        if (!onBackground) {
            server.join()
        }
    }

    public fun stop() {
        server.stop()
        server.join()
    }

    public fun join() {
        server.join()
    }

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doRequest(req, resp, RequestMethod.GET)
    }

    fun addBlueprints(vararg blueprints: Blueprint) {
        blueprints.forEach {
            it.javaClass.getMethods()
                    .map { it to it.getAnnotation(javaClass<Route>()) }
                    .filter { it.second != null }
                    .forEach {
                        println(it)
                    }
        }
    }

    fun doRequest(req: HttpServletRequest?, resp: HttpServletResponse?, method: RequestMethod) {
        if (req == null) {
            return
        }
        if (resp == null) {
            return
        }
        if (method == RequestMethod.POST) {
            throw NotImplementedException()
        }
        val handler = router.findHandler(req.getRequestURI().toString())
        if (handler == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND)
            return
        }
        val response = handler()
        resp.setStatus(HttpServletResponse.SC_OK)
        val writer = resp.getWriter()
        writer.write(response)
        writer.close()
    }

    fun addOnServerReady(listener: (app: Klask) -> Unit) {
        serverReadyListeners.add(listener)
    }
}

class Router(val app: Klask) {
    fun findHandler(requestURI: String): (() -> String)? {
        val handler = app.javaClass.getMethods()
                .map { it to it.getAnnotation(javaClass<Route>()) }
                .filter { it.second != null }
                .firstOrNull {
                    it.second.rule == requestURI
                }
        if (handler == null) {
            return null
        }
        return {
            handler.first.invoke(app) as String
        }
    }
}
