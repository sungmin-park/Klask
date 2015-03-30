package com.klask.urlfor

import com.klask.Klask
import com.klask.blueprint.Blueprint
import com.klask.router.Route
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

object users : Blueprint() {
    Route("/<id:int>")
    fun show(id: Int) {
        return
    }
}

object app : Klask() {
    init {
        addBlueprint(users, "/users")
    }

    Route("/")
    fun index() {
    }

    Route("/novels/<name>")
    fun show() {
    }
}

class UrlForTest {
    Before
    fun before() {
        app.pushContext()
    }

    After
    fun after() {
        app.popContext()
    }

    Test
    fun testUrlFor() {
        Assert.assertEquals("/", urlfor("index"))
    }

    Test
    fun testStringPathVariable() {
        Assert.assertEquals("/novels/The-name-of-the-rose", urlfor("show", "name" to "The-name-of-the-rose"))
    }

    Test
    fun testNested() {
        Assert.assertEquals("/users/1", urlfor("users.show", "id" to 1))
    }
}
