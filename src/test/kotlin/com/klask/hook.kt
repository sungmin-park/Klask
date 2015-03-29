package com.klask.hook

import com.klask.Klask
import com.klask.router.Route
import org.junit.Assert
import org.junit.Test

object app : Klask() {
    public var teardownRequestCalled: Boolean = false

    Route("/")
    fun index() {
    }

    override fun onTearDownRequest() {
        teardownRequestCalled = true
        super.onTearDownRequest()
    }
}

class HookTest {
    Test
    fun testTearDownRequest() {
        app.teardownRequestCalled = false
        app.client.get("/")
        Assert.assertEquals(true, app.teardownRequestCalled)
    }
}
