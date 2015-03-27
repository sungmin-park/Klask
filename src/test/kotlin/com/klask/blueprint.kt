package com.klask.blueprint

import com.klask.Klask
import com.klask.router.Route
import org.junit.Test
import org.junit.Assert

object user : Blueprint() {
    Route("/")
    fun list(): String {
        return "user.list"
    }
}

object admin : Blueprint() {
    init {
        addBlueprints(user)
    }
}

object article : Blueprint() {
    Route("/")
    fun list(): String {
        return "article.list"
    }
}

object dashBoard : Blueprint() {
    Route("/")
    fun index(): String {
        return "dashBoard.index"
    }
}

object front : Blueprint() {
    init {
        addBlueprints(dashBoard, article)
    }
}

object app : Klask() {
    init {
        addBlueprints(front, admin)
    }

    Route("/")
    fun index(): String {
        return "index"
    }
}


class BlueprintTest {
    Test
    fun testNestedRoute() {
        Assert.assertEquals("index", app.client.get("/").data)
        Assert.assertEquals("dashboard.index", app.client.get("/dashBoard/").data)
    }
}
