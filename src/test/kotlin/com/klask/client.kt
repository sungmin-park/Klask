package com.klask.client

import org.junit.Test
import com.klask.Klask
import com.klask.router.Route
import org.junit.Assert
import javax.servlet.http.HttpServletResponse
import com.klask.RequestMethod


object app : Klask() {
    Route("/")
    fun index(): String {
        return "index"
    }
}

class ClientTest {
    Test
    fun testRequest() {
        val res = app.client.request(url = "/", method = RequestMethod.GET)
        Assert.assertEquals(HttpServletResponse.SC_OK, res.statusCode)
        Assert.assertEquals("index", res.data)
    }

    Test
    fun testGet() {
        val res = app.client.get("/")
        Assert.assertEquals(HttpServletResponse.SC_OK, res.statusCode)
        Assert.assertEquals("index", res.data)
    }
}
