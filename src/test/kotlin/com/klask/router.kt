package com.klask

import org.junit.Test
import com.klask.router.Route
import org.junit.Assert

object app : Klask() {
    Route("/")
    fun index() {
    }

    Route("/post/<name>")
    fun postShow() {
    }
}

class RouterTest {
    Test
    fun testExactMatch() {
        Assert.assertEquals("/", app.router.findHandlerChain("/")?.rule)
        Assert.assertEquals(null, app.router.findHandlerChain("/not-exist-url"))
    }

    Test
    fun testStringPathVariable() {
        val chain = app.router.findHandlerChain("/post/first post")
        Assert.assertEquals("/post/<name>", chain?.rule)
    }
}
