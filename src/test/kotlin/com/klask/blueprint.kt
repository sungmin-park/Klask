package com.klask.blueprint

import com.klask.Klask
import com.klask.router.Route
import org.junit.Assert
import org.junit.Test

object user : Blueprint() {
    Route("/")
    fun list(): String {
        return "user.list"
    }
}

object admin : Blueprint() {
    init {
        addBlueprint(user, "/user")
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
        addBlueprint(dashBoard)
        addBlueprint(article, urlPrefix = "/article")
    }
}

object app : Klask() {
    init {
        addBlueprint(front, urlPrefix = "/front")
        addBlueprint(admin, urlPrefix = "/admin")
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
        Assert.assertEquals("dashBoard.index", app.client.get("/front/").data)
    }
}
