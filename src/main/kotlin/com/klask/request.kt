package com.klask.requests

import com.klask.router.Handler
import com.klask.sessions.Session
import javax.servlet.http.HttpServletRequest

public trait Request {
    public val values: Map<String, Array<String>>
    public val session: Session
    public val path: String
    public val endpoint: String
}

class RequestImpl(val servlet: HttpServletRequest, public override val session: Session, val handler: Handler?) : Request {
    override val endpoint: String
        get() = handler?.endpoint ?: ""

    override val path: String
        get() = servlet.getRequestURI()

    override val values: Map<String, Array<String>>
        get() = servlet.getParameterMap()
}
