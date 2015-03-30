package com.klask

import com.klask.blueprint.Blueprint
import com.klask.blueprint.BlueprintJar
import com.klask.client.Client
import com.klask.jetty.JettyServer
import com.klask.jetty.KlaskServerListener
import com.klask.router.Route
import com.klask.router.Router
import com.klask.servlet.KlaskHttpServlet
import ko.html.Element
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.springframework.core.DefaultParameterNameDiscoverer
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.File
import java.io.Writer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.properties.Delegates

enum class RequestMethod {GET POST }

abstract class Application {
    public val name: String
        get() = javaClass.getSimpleName()
    public val router: Router = Router(this)
    val blueprintJars = arrayListOf<BlueprintJar>()

    protected fun addBlueprint(front: Blueprint, urlPrefix: String = "") {
        blueprintJars.add(BlueprintJar(front, urlPrefix))
    }

    open fun onTearDownRequest() {
    }
}

abstract class Response(public val statusCode: Int, public val contentType: String? = null, val charset: String? = null) {
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
        Response(statusCode = statusCode, contentType = "text/html", charset = "utf-8") {
    override val data: String
        get() = content

    override fun write(writer: Writer) {
        writer.write(content)
    }
}

class ElementResponse(private val element: Element, statusCode: Int = HttpServletResponse.SC_OK) :
        Response(statusCode = statusCode, contentType = "text/html", charset = "utf-8") {
    override val data: String
        get() = element.toString()

    override fun write(writer: Writer) {
        element.render(writer)
    }
}

trait KlaskApp {
    public val name: String
    public val router: Router
}

open class Klask : Application(), KlaskApp {
    public var server: JettyServer by Delegates.notNull()
    private val servlet = KlaskHttpServlet(this)
    public val client: Client = Client(this)
    public val staticPath: File

    private val serverReadyListeners = arrayListOf<(app: Klask) -> Unit>()
    private val serverListener = KlaskServerListener(this, serverReadyListeners);

    init {
        staticPath = File(javaClass.getResource("/static").getPath())
    }


    public fun run(port: Int = 8080, onBackground: Boolean = false) {
        server = JettyServer(port)

        val servletContextHandler = ServletContextHandler()
        servletContextHandler.setContextPath("/")
        servletContextHandler.setResourceBase(staticPath.getParent())
        servletContextHandler.addServlet(ServletHolder(DefaultServlet()), "/static/*")
        servletContextHandler.addServlet(ServletHolder(servlet), "/*")

        server.setHandler(servletContextHandler)
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
        pushContext()
        try {
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
                resp.setContentType(response.contentType)
                resp.setCharacterEncoding(response.charset)
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
        } finally {
            popContext()
        }
    }

    fun doRequest(req: HttpServletRequest?, resp: HttpServletResponse?, method: RequestMethod) {
        if (method == RequestMethod.POST) {
            throw NotImplementedException()
        }
        processRequest(req = req!!, resp = resp!!, method = method)
    }

    Route("/static/<fileName:path>")
    open fun static(fileName: String): String {
        return File(staticPath, fileName).readText()
    }

    public fun context<T>(use: () -> T): T {
        pushContext()
        try {
            return use()
        } finally {
            popContext()
        }
    }

    public fun popContext() {
        assert(local.get() == this)
        local.remove()
    }

    public fun pushContext() {
        assert(local.get() == null)
        local.set(this)
    }

    companion object {
        private val local = ThreadLocal<KlaskApp>()
        public val currentApp: KlaskApp
            get() = local.get()
    }
}

object currentApp : KlaskApp {
    override val router: Router
        get() = Klask.currentApp.router

    override val name: String
        get() = Klask.currentApp.name
}
