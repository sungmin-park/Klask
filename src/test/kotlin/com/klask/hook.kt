package com.klask.hook

import com.klask.Klask
import com.klask.Response
import com.klask.StringResponse
import com.klask.blueprint.Blueprint
import com.klask.request
import com.klask.router.Route
import org.junit.Assert
import org.junit.Test


object grandChild : Blueprint() {
    Route("/")
    fun index(): String {
        return name
    }

    Route("/content")
    fun content(): String {
        return name
    }

    override fun onBeforeRequest(): Response? {
        super.onBeforeRequest()
        val root = Klask.currentApp as App
        root.beforeRequests = root.beforeRequests + listOf(name)
        return null
    }
}

object child : Blueprint() {
    init {
        addBlueprint(grandChild, urlPrefix = "/grandChild")
    }

    Route("/")
    fun index() {
    }

    override fun onBeforeRequest(): Response? {
        super.onBeforeRequest()
        val root = Klask.currentApp as App
        root.beforeRequests = root.beforeRequests + listOf(name)
        if (request.endpoint == "child.grandChild.index") {
            return StringResponse(name)
        }
        return null
    }
}


class App : Klask() {
    var beforeRequests = listOf<String>()

    init {
        addBlueprint(child, urlPrefix = "/child")
    }

    Route("/")
    fun index() {
    }

    override fun onBeforeRequest(): Response? {
        beforeRequests = beforeRequests + listOf(name)
        return null
    }
}


class HookTest {
    Test
    fun testTearDownRequest() {
        var teardownRequestCalled = false
        val app = object : Klask() {
            override fun onTearDownRequest() {
                teardownRequestCalled = true
                super.onTearDownRequest()
            }
        }
        app.client.get("/")
        Assert.assertTrue(teardownRequestCalled)
    }

    Test
    fun testOnBeforeRequest() {
        App().let { app ->
            app.client.get("/404")
            Assert.assertEquals(listOf<String>(), app.beforeRequests)

        }

        App().let { app ->
            app.client.get("/")
            Assert.assertEquals(listOf("App"), app.beforeRequests)
        }

        App().let { app ->
            app.client.get("/child/")
            Assert.assertEquals(listOf("App", "child"), app.beforeRequests)
        }

        App().let { app ->
            Assert.assertEquals("child", app.client.get("/child/grandChild/").data)
            Assert.assertEquals(listOf("App", "child"), app.beforeRequests)
        }

        App().let { app ->
            Assert.assertEquals("grandChild", app.client.get("/child/grandChild/content").data)
            Assert.assertEquals(listOf("App", "child", "grandChild"), app.beforeRequests)
        }
    }
}
