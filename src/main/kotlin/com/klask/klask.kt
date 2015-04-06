package com.klask

import com.khtml.Node
import com.klask.blueprint.Blueprint
import com.klask.blueprint.BlueprintJar
import com.klask.client.Client
import com.klask.jetty.JettyServer
import com.klask.jetty.KlaskServerListener
import com.klask.requests.Request
import com.klask.requests.RequestImpl
import com.klask.requests.Values
import com.klask.router.Handler
import com.klask.router.Route
import com.klask.router.Router
import com.klask.servlet.KlaskHttpServlet
import com.klask.sessions.Session
import com.klask.sessions.SessionImpl
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.springframework.core.DefaultParameterNameDiscoverer
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.File
import java.io.Writer
import javax.servlet.http.Cookie
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

    open public fun onBeforeRequest(): Response? {
        return null
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

class NodeResponse(private val node: Node, statusCode: Int = HttpServletResponse.SC_OK) :
        Response(statusCode = statusCode, contentType = "text/html", charset = "utf-8") {
    override val data: String
        get() = node.toString()

    override fun write(writer: Writer) {
        node.render(writer)
    }
}

class RedirectResponse(val location: String) : Response(statusCode = 302) {
    override val data: String
        get() = ""

    override fun write(writer: Writer) {
    }
}

trait KlaskApp {
    public val name: String
    public val router: Router
    public val requestLocal: ThreadLocal<Request>
}

open class Klask : Application(), KlaskApp {
    public var server: JettyServer by Delegates.notNull()
    private val servlet = KlaskHttpServlet(this)
    public val client: Client
        get() = Client(this)
    public val staticPath: File

    private val serverReadyListeners = arrayListOf<(app: Klask) -> Unit>()
    private val serverListener = KlaskServerListener(this, serverReadyListeners);
    override public val requestLocal = ThreadLocal<Request>()

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

    fun <T> processRequestContext(req: HttpServletRequest, resp: HttpServletResponse, method: RequestMethod, context: Klask.(handler: Handler?, resp: HttpServletResponse, session: SessionImpl) -> T): T {
        pushContext()
        val cookie = req.getCookies()?.firstOrNull { it.getName() == "session" }
        val session = SessionImpl(cookie)
        val request = RequestImpl(req, session, router.findHandler(req.getRequestURI().toString()))
        requestLocal.set(request)
        try {
            try {
                return context(request.handler, resp, session)
            } finally {
                onTearDownRequest()
            }
        } finally {
            requestLocal.remove()
            popContext()
        }
    }

    fun processRequest(req: HttpServletRequest, resp: HttpServletResponse, method: RequestMethod): Response {
        return processRequestContext(req, resp, method) { handler, resp, session ->
            processResponse(handler, resp, session)
        }
    }

    private fun processResponse(handler: Handler?, resp: HttpServletResponse, session: SessionImpl): Response {
        val result: Any =
                if (handler == null) {
                    EmptyResponse(HttpServletResponse.SC_NOT_FOUND)
                } else {
                    processHandlerMethod(handler)
                }
        val response: Response = when (result) {
            is Response -> result
            is String -> StringResponse(content = result)
            is Node -> NodeResponse(node = result)
            else -> throw IllegalArgumentException()
        }
        resp.setContentType(response.contentType)
        resp.setCharacterEncoding(response.charset)
        val secure_cookie = session.serialize()
        if (secure_cookie != null) {
            resp.addCookie(Cookie("session", secure_cookie).let {
                it.setPath("/")
                it
            })
        }
        when (response) {
            is RedirectResponse -> resp.sendRedirect(response.location)
            else -> {
                when (response.statusCode) {
                    HttpServletResponse.SC_OK -> resp.setStatus(response.statusCode)
                    else ->
                        resp.sendError(response.statusCode)
                }
            }
        }
        if (!resp.isCommitted()) {
            resp.getWriter().use { response.write(it) }
        }
        return response
    }

    private fun processHandlerMethod(handler: Handler): Any {
        var appChain = arrayListOf<Application>()
        for (it in handler.appChain.reverse()) {
            val beforeResponse = it.onBeforeRequest()
            appChain.add(it)
            if (beforeResponse != null) {
                return beforeResponse
            }
        }
        val defaultParameterNameDiscoverer = DefaultParameterNameDiscoverer()
        val args = defaultParameterNameDiscoverer.getParameterNames(handler.method)
                .map { handler.parseResult?.pathVariables?.get(it) }
                .copyToArray()
        val res = handler.method.invoke(handler.appChain.first(), *args)
        return res ?: EmptyResponse(HttpServletResponse.SC_OK)
    }

    open fun onTearDownRequest() {
    }

    fun doRequest(req: HttpServletRequest?, resp: HttpServletResponse?, method: RequestMethod) {
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
    override val requestLocal: ThreadLocal<Request>
        get() = Klask.currentApp.requestLocal

    override val router: Router
        get() = Klask.currentApp.router

    override val name: String
        get() = Klask.currentApp.name
}

public object request : Request {
    override val isGet: Boolean
        get() = r.isGet

    override val isPost: Boolean
        get() = r.isPost

    override val method: RequestMethod
        get() = r.method

    override val endpoint: String
        get() = r.endpoint

    private val r: Request
        get() = currentApp.requestLocal.get()

    override val path: String
        get() = r.path

    override val session: Session
        get() = r.session

    override val values: Values
        get() = r.values
}
