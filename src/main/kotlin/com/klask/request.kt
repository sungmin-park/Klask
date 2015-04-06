package com.klask.requests

import com.klask.RequestMethod
import com.klask.router.Handler
import com.klask.sessions.Session
import javax.servlet.http.HttpServletRequest

class Values(map: Map<String, Array<String>>) : Map<String, Array<String>> by map {
    fun get(key: String, default: String): String {
        return this[key].orEmpty().firstOrNull() ?: default
    }
}

public trait Request {
    public val values: Values
    public val session: Session
    public val path: String
    public val endpoint: String
    public val method: RequestMethod
    public val isGet: Boolean
    public val isPost: Boolean
}

class RequestImpl(val servlet: HttpServletRequest, public override val session: Session, val handler: Handler?) : Request {
    override val method: RequestMethod = RequestMethod.valueOf(servlet.getMethod())
    override val isGet: Boolean = method == RequestMethod.GET
    override val isPost: Boolean = method == RequestMethod.POST

    override val endpoint: String
        get() = handler?.endpoint ?: ""

    override val path: String
        get() = servlet.getRequestURI()

    override val values: Values
        get() = Values(servlet.getParameterMap() ?: mapOf())
}
