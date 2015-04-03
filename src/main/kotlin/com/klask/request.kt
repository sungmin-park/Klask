package com.klask.requests

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
}

class RequestImpl(val servlet: HttpServletRequest, public override val session: Session, val handler: Handler?) : Request {
    override val endpoint: String
        get() = handler?.endpoint ?: ""

    override val path: String
        get() = servlet.getRequestURI()

    override val values: Values
        get() = Values(servlet.getParameterMap())
}
