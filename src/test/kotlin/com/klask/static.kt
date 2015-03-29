package com.klask.static

import com.klask.Klask
import org.junit.Assert
import org.junit.Test

object app : Klask() {
}

class StaticTest {
    Test
    fun testStatic() {
        Assert.assertEquals("welcome", app.client.get("/static/welcome.txt").data)
    }
}
