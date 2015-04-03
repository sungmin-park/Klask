package com.klask

import com.klask.router.Route
import org.junit.Assert
import org.junit.Test

object app : Klask() {
    Route("/")
    fun index(): String? {
        return request.values["name"]?.join()
    }

    Route("/context")
    fun context() {
    }
}

class RequestTest {
    Test
    fun testGet() {
        Assert.assertEquals("steve", app.client.get("/?name=steve").data)
    }

    Test
    fun testContext() {
        app.client.context("/context?key=value") {
            Assert.assertArrayEquals(array("value"), request.values["key"])
        }
    }

    Test
    fun testPath() {
        app.client.context("/context?key=value") {
            Assert.assertEquals("/context", request.path)
        }
    }
}
