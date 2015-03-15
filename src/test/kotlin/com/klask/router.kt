package com.klask

import org.junit.Test
import com.klask.router.Route
import org.junit.Assert

object app : Klask() {
    Route("/")
    fun index() {
    }
}

class RouterTest {
    Test
    fun testExactMatch() {
        val chain = app.router.findHandlerChain("/")
        Assert.assertEquals("/", chain?.rule)
    }
}
