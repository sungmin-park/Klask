package com.klask

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.properties.Delegates
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import com.klask.router.Router
import com.klask.servlet.KlaskHttpServlet
import com.klask.jetty.KlaskServerListener
import java.io.Writer

enum class RequestMethod {GET POST }

abstract class KlaskApp {
    val router = Router(this)
}

abstract class Response {
    abstract fun write(writer: Writer)
}

class StringResponse(val content: String) : Response() {
    override fun write(writer: Writer) {
        writer.write(content)
    }
}

open class Klask : KlaskApp() {
    var server: Server by Delegates.notNull()
    val servlet = KlaskHttpServlet(this)

    private val serverReadyListeners = arrayListOf<(app: Klask) -> Unit>()
    private val serverListener = KlaskServerListener(this, serverReadyListeners);

    public fun run(port: Int = 8080, onBackground: Boolean = false) {
        server = Server(port)
        val handler = ServletHandler()
        handler.addServletWithMapping(ServletHolder(servlet), "/*")
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
        val result = handler.method.invoke(handler.appChain.last())
        [suppress("USELESS_CAST_STATIC_ASSERT_IS_FINE")]
        val response = when (result) {
            is Response -> result as Response
            is String -> StringResponse(content = result)
            else -> throw IllegalArgumentException()
        }
        resp.setStatus(HttpServletResponse.SC_OK)
        val writer = resp.getWriter()
        response.write(writer)
        writer.close()
    }

    fun addOnServerReady(listener: (app: Klask) -> Unit) {
        serverReadyListeners.add(listener)
    }
}
