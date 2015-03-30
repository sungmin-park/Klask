package com.klask.context

import com.klask.Klask
import com.klask.currentApp
import com.klask.router.Route
import org.junit.Assert
import org.junit.Test
import kotlin.properties.Delegates

object app : Klask() {
    Route("/")
    fun index(): String {
        return currentApp.name
    }
}

class ContextTest {
    Test
    fun testAppContext() {
        app.context {
            Assert.assertEquals("app", currentApp.name)
        }
    }

    Test
    fun testAppContextInRequest() {
        Assert.assertEquals("app", app.client.get("/").data)
    }
}

