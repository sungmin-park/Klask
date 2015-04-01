package com.klask

import com.klask.router.Route
import org.junit.Assert
import org.junit.Test

object app : Klask() {
    Route("/")
    fun index(): String? {
        return request.values["name"]?.join()
    }
}

class RequestTest {
    Test
    fun testGet() {
        Assert.assertEquals("steve", app.client.get("/?name=steve").data)
    }
}
