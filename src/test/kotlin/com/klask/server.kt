package com.klask.server

import org.junit.Test
import com.klask.Klask
import com.klask.Route
import java.util.concurrent.CountDownLatch
import java.net.URL
import org.junit.Assert
import org.junit.Before
import org.junit.After
import java.util.concurrent.Semaphore

object app : Klask() {
    Route("/")
    fun index(): String {
        return "index"
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
        Assert.assertEquals(
                "index", URL("http://localhost:8080").openConnection().getInputStream().reader("UTF-8").readText()
        )
    }
}
