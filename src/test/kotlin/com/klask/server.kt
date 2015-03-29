package com.klask.server

import com.klask.Klask
import com.klask.router.Route
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

object app : Klask() {
    Route("/")
    fun index(): String {
        return "index"
    }

    Route("/static/<fileName:path>")
    override fun static(fileName: String):String {
        throw IllegalArgumentException(fileName)
    }
}

class ServerTest {
    Before
    fun before() {
        app.run(onBackground = true)
    }

    After
    fun after() {
        app.stop()
    }

    Test
    fun testRun() {
        Assert.assertEquals("index", app.server.get("/"))
    }

    Test
    fun testStatic() {
        Assert.assertEquals("welcome", app.server.get("/static/welcome.txt"))
    }
}
