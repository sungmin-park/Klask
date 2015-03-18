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
import com.klask.client.Client

enum class RequestMethod {GET POST }

abstract class KlaskApp {
    val router = Router(this)
}

abstract class Response(public val statusCode: Int) {
    public abstract val data: String
    abstract fun write(writer: Writer)
}

class EmptyResponse(statusCode: Int) : Response(statusCode = statusCode) {
    override val data: String
        get() = ""

    override fun write(writer: Writer) {
    }
}

class StringResponse(private val content: String, statusCode: Int = HttpServletResponse.SC_OK) :
        Response(statusCode = statusCode) {
    override val data: String
        get() = content

    override fun write(writer: Writer) {
        writer.write(content)
    }
}

open class Klask : KlaskApp() {
    private var server: Server by Delegates.notNull()
    private val servlet = KlaskHttpServlet(this)
    public val client: Client = Client(this)

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

    fun processRequest(req: HttpServletRequest, resp: HttpServletResponse, method: RequestMethod): Response {
        val handler = router.findHandler(req.getRequestURI().toString())
        if (handler == null) {
            return EmptyResponse(HttpServletResponse.SC_NOT_FOUND)
        }
        val result = handler.method.invoke(handler.appChain.last())
        [suppress("USELESS_CAST_STATIC_ASSERT_IS_FINE")]
        val response = when (result) {
            is Response -> result as Response
            is String -> StringResponse(content = result)
            else -> throw IllegalArgumentException()
        }
        resp.setStatus(response.statusCode)
        resp.getWriter().use { response.write(it) }
        return response
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
        processRequest(req = req, resp = resp, method = method)
    }

    fun addOnServerReady(listener: (app: Klask) -> Unit) {
        serverReadyListeners.add(listener)
    }
}
