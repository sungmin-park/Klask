package com.klask

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.servlet.ServletHolder

fun main(args: Array<String>) {
    val server = Server(8080)
    val handler = ServletHandler()
    handler.addServletWithMapping(ServletHolder(App()) , "/*")
    server.setHandler(handler)
    server.start()
    server.join()
}

class App : HttpServlet() {
    {
        println("App!!")
    }
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if (resp == null) {
            return
        }
        resp.setContentType("text/html")
        resp.setStatus(HttpServletResponse.SC_OK)
        resp.getWriter().println("App !")
    }
}
