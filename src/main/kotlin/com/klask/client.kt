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

    fun request(url: String, method: RequestMethod): Response {
        val request = ClientHttpServletRequest(url, cookies)
        val response = ClientHttpServletResponse()
        return app.processRequest(req = request, resp = response, method = method).let {
            cookies = response.cookies.copyToArray()
            it
        }
    }
}
