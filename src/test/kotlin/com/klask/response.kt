package com.klask.response

import com.klask.Klask
import com.klask.router.Route
import ko.html.Div
import ko.html.Html
import org.junit.Assert
import org.junit.Test

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

    Route("/korean.html")
    fun koreanHtml(): Div {
        return Div(text = "한글")
    }

    Route("/korean.text")
    fun koreanText(): String {
        return "한글"
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
        val response = app.client.get("/html")
        Assert.assertEquals("<!DOCTYPE html><html><body><h1>html</h1></body></html>", response.data)
        Assert.assertEquals("text/html", response.contentType)
    }

    Test
    fun testKorean() {
        Assert.assertEquals("<div>한글</div>", app.client.get("/korean.html").data)
        Assert.assertEquals("한글", app.client.get("/korean.text").data)
        app.run(onBackground = true)
        Assert.assertEquals("<div>한글</div>", app.server.get("/korean.html"))
        Assert.assertEquals("한글", app.server.get("/korean.text"))
        app.stop()
    }
}
