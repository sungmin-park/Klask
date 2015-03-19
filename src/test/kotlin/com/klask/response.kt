package com.klask.response

import org.junit.Test
import com.klask.Klask
import com.klask.router.Route
import org.junit.Assert

object app : Klask() {
    Route("/")
    fun index(): String {
        return "index"
    }

    Route("/unit")
    fun unit() {
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
}
