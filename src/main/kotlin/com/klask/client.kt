package com.klask.client

import com.klask.Klask
import com.klask.Response
import com.klask.RequestMethod

public class Client(val app: Klask) {
    public fun get(url: String): Response {
        return request(url = url, method = RequestMethod.GET)
    }

    fun request(url: String, method: RequestMethod): Response {
        val request = ClientHttpServletRequest(url)
        val response = ClientHttpServletResponse()
        return app.processRequest(req = request, resp = response, method = method)
    }
}
