package com.klask.client

import com.klask.Klask
import com.klask.RequestMethod
import com.klask.Response
import javax.servlet.http.Cookie

public class Client(val app: Klask) {
    var cookies = array<Cookie>()

    public fun get(url: String): Response {
        return request(url, RequestMethod.GET)
    }

    public fun post(url: String, data: List<Pair<String, String>> = listOf()): Response {
        return request(url, RequestMethod.POST, data)
    }

    fun request(url: String, method: RequestMethod, data: List<Pair<String, String>> = listOf()): Response {
        val request = ClientHttpServletRequest(
                url, cookies, method,
                data.map { it.first }
                        .toSet()
                        .map { key -> key to data.filter { it.first == key }.map { it.second } }
                        .toMap()
        )
        val response = ClientHttpServletResponse()
        return app.processRequest(request, response).let {
            cookies = response.cookies.copyToArray()
            it
        }
    }

    fun context(url: String, method: RequestMethod = RequestMethod.GET, context: () -> Unit) {
        val request = ClientHttpServletRequest(url, cookies, method, mapOf())
        val response = ClientHttpServletResponse()
        app.processRequestContext<Unit>(request, response) { a, b, c ->
            context()
        }
    }
}
