package com.klask

import com.klask.blueprint.Blueprint
import com.klask.router.Route
import org.junit.Assert
import org.junit.Test

object books : Blueprint() {
    Route("/")
    fun list() {
    }
}

object user : Blueprint() {
    init {
        addBlueprint(books, urlPrefix = "/books")
    }
}

object app : Klask() {
    init {
        addBlueprint(user, urlPrefix = "/user")
    }

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
    fun testGetParameter() {
        Assert.assertEquals("steve", app.client.get("/?name=steve").data)
    }

    Test
    fun testIsGet() {
        app.client.context("/") {
            Assert.assertEquals(RequestMethod.GET, request.method)
            Assert.assertTrue(request.isGet)
            Assert.assertFalse(request.isPost)
        }
    }

    Test
    fun testIsPost() {
        app.client.context("/", method = RequestMethod.POST) {
            Assert.assertEquals(RequestMethod.POST, request.method)
            Assert.assertFalse(request.isGet)
            Assert.assertTrue(request.isPost)
        }
    }

    Test
    fun testGetDefault() {
        app.client.context("/") {
            Assert.assertEquals("steve", request.values.get("name", "steve"))
        }
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

    Test
    fun testEndpoint() {
        app.client.context("/user/books/") {
            Assert.assertEquals("user.books.list", request.endpoint)
        }
    }
}
