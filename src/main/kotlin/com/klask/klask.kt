package com.klask

import com.klask.blueprint.Blueprint
import com.klask.blueprint.BlueprintJar
import com.klask.client.Client
import com.klask.jetty.KlaskServerListener
import com.klask.router.Route
import com.klask.router.Router
import com.klask.servlet.KlaskHttpServlet
import ko.html.Element
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.springframework.core.DefaultParameterNameDiscoverer
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.File
import java.io.Writer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.properties.Delegates

enum class RequestMethod {GET POST }

abstract class KlaskApp {
    val router: Router = Router(this)
    val blueprintJars = arrayListOf<BlueprintJar>()

    protected fun addBlueprint(front: Blueprint, urlPrefix: String = "") {
        blueprintJars.add(BlueprintJar(front, urlPrefix))
    }

    open fun onTearDownRequest() {
    }
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

class ElementResponse(private val element: Element, statusCode: Int = HttpServletResponse.SC_OK) :
        Response(statusCode = statusCode) {
    override val data: String
        get() = element.toString()

    override fun write(writer: Writer) {
        element.render(writer)
    }
}

open class Klask : KlaskApp() {
    private var server: Server by Delegates.notNull()
    private val servlet = KlaskHttpServlet(this)
    public val client: Client = Client(this)
    public val staticPath: File

    private val serverReadyListeners = arrayListOf<(app: Klask) -> Unit>()
    private val serverListener = KlaskServerListener(this, serverReadyListeners);

    init {
        staticPath = File(javaClass.getResource("/static").getPath())
    }


    public fun run(port: Int = 8080, onBackground: Boolean = false) {
        server = Server(port)
        val handler = ServletHandler()
        handler.addServletWithMapping(ServletHolder(servlet), "/*")
        server.setHandler(handler)
        server.addLifeCycleListener(serverListener)
        server.start()
        if (!onBackground) {
            join()
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
        try {
            val result: Any =
                    if (handler == null) {
                        EmptyResponse(HttpServletResponse.SC_NOT_FOUND)
                    } else {
                        val defaultParameterNameDiscoverer = DefaultParameterNameDiscoverer()
                        val args = defaultParameterNameDiscoverer.getParameterNames(handler.method)
                                .map { handler.parseResult?.pathVariables?.get(it) }
                                .copyToArray()
                        val res = handler.method.invoke(handler.appChain.first(), *args)
                        res ?: EmptyResponse(HttpServletResponse.SC_OK)
                    }
            val response: Response = when (result) {
                is Response -> result
                is String -> StringResponse(content = result)
                is Element -> ElementResponse(element = result)
                else -> throw IllegalArgumentException()
            }
            when (response.statusCode) {
                HttpServletResponse.SC_OK -> resp.setStatus(response.statusCode)
                else ->
                    resp.sendError(response.statusCode)
            }
            if (!resp.isCommitted()) {
                resp.getWriter().use { response.write(it) }
            }
            return response
        } finally {
            if (handler != null) {
                handler.appChain.forEach {
                    it.onTearDownRequest()
                }
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
        processRequest(req = req, resp = resp, method = method)
    }

    Route("/static/<fileName:path>")
    fun static(fileName: String): String {
        return File(staticPath, fileName).readLines().join("\n")
    }
}
