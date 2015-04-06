package com.klask.client

import com.klask.Klask
import com.klask.RequestMethod
import com.klask.Response
import javax.servlet.http.Cookie

public class Client(val app: Klask) {
    var cookies = array<Cookie>()

    public fun get(url: String): Response {
        return request(url = url, method = RequestMethod.GET)
    }

    public fun post(url: String): Response {
        return request(url = url, method = RequestMethod.POST)
    }

    fun request(url: String, method: RequestMethod): Response {
        val request = ClientHttpServletRequest(url, cookies, method)
        val response = ClientHttpServletResponse()
        return app.processRequest(request, response).let {
            cookies = response.cookies.copyToArray()
            it
        }
    }

    fun context(url: String, method: RequestMethod = RequestMethod.GET, context: () -> Unit) {
        val request = ClientHttpServletRequest(url, cookies, method)
        val response = ClientHttpServletResponse()
        app.processRequestContext<Unit>(request, response) { a, b, c ->
            context()
        }
    }
}
