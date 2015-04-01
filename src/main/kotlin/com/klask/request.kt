package com.klask.requests

import com.klask.sessions.Session
import javax.servlet.http.HttpServletRequest

public trait Request {
    public val values: Map<String, Array<String>>
    public val session: Session
}

class RequestImpl(val servlet: HttpServletRequest, public override val session: Session) : Request {
    override val values: Map<String, Array<String>>
        get() = servlet.getParameterMap()
}
