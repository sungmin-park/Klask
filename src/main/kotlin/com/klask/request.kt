package com.klask.requests

import javax.servlet.http.HttpServletRequest

public trait Request {
    public val values: Map<String, Array<String>>
}

class RequestImpl(val servlet: HttpServletRequest) : Request {
    override val values: Map<String, Array<String>>
        get() = servlet.getParameterMap()
}
