package com.klask.servlet

import com.klask.Klask
import com.klask.RequestMethod
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class KlaskHttpServlet(val app: Klask) : HttpServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        app.doRequest(req, resp, RequestMethod.GET)
    }

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        app.doRequest(req, resp, RequestMethod.POST)
    }
}

