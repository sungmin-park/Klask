package com.klask.servlet

import com.klask.Klask
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.klask.RequestMethod

class KlaskHttpServlet(val app: Klask) : HttpServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        app.doRequest(req, resp, RequestMethod.GET)
    }
}

