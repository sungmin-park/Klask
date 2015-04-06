package com.klask.client

import com.klask.Klask
import com.klask.RequestMethod
import com.klask.request
import com.klask.router.Route
import org.junit.Assert
import org.junit.Test
import javax.servlet.http.HttpServletResponse


object app : Klask() {
    Route("/")
    fun index(): String {
        return "index"
    }

    Route("/get")
    fun get(): String {
        return "get"
    }

    Route("/post")
    fun post(): String {
        return "post"
    }

    Route("/parameter")
    fun parameter(): String {
        return request.values.get("name", "")
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
        val res = app.client.get("/get")
        Assert.assertEquals(HttpServletResponse.SC_OK, res.statusCode)
        Assert.assertEquals("get", res.data)
        Assert.assertEquals("steve", app.client.get("/parameter?name=steve").data)
    }

    Test
    fun testPost() {
        val res = app.client.post("/post")
        Assert.assertEquals(HttpServletResponse.SC_OK, res.statusCode)
        Assert.assertEquals("post", res.data)
        Assert.assertEquals("steve", app.client.post("/parameter", listOf("name" to "steve")).data)
    }
}
