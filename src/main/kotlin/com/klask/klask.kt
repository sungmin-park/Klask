package com.klask

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import javax.servlet.http.HttpServlet
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Response

Retention(RetentionPolicy.RUNTIME)
annotation class Route(val rule: String)

open class Blueprint

enum class RequestMethod {GET POST }

open class Klask : HttpServlet() {
    val router = Router(this);

    public fun run(port: Int = 8080) {
        val server = Server(port)
        val handler = ServletHandler()
        handler.addServletWithMapping(ServletHolder(this), "/*")
        server.setHandler(handler)
        server.start()
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
