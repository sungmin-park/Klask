package com.klask.response

import org.junit.Test
import com.klask.Klask
import com.klask.router.Route
import org.junit.Assert
import ko.html.Html

object app : Klask() {
    Route("/")
    fun index(): String {
        return "index"
    }

    Route("/unit")
    fun unit() {
    }

    Route("/html")
    fun html(): Html {
        return Html {
            body {
                h1(text = "html")
            }
        }
    }
}

class ResponseTest {
    Test
    fun testStringResponse() {
        Assert.assertEquals("index", app.client.get("/").data)
    }

    Test
    fun testUnitResponse() {
        Assert.assertEquals("", app.client.get("/unit").data)
    }

    Test
    fun testHtml() {
        Assert.assertEquals("<!DOCTYPE html><html><body><h1>html</h1></body></html>", app.client.get("/html").data)
    }
}
