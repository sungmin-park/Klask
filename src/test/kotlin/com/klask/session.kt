package com.klask.session

import com.klask.Klask
import com.klask.request
import com.klask.router.Route
import org.junit.Assert
import org.junit.Test

object app : Klask() {
    Route("/set")
    fun set() {
        request.session["name"] = request.values["name"]?.get(0)
    }

    Route("/get")
    fun get(): String? {
        return request.session["name"] as String?
    }
}

class TestSession {
    Test
    fun testGet() {
        val client = app.client
        client.get("/set?name=steve")
        Assert.assertEquals("steve", client.get("/get").data)
    }
}

