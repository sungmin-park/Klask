package com.klask

import com.klask.router.*
import org.junit.Assert
import org.junit.Test

object app : Klask() {
    Route("/")
    fun index() {
    }

    Route("/post/<name>")
    fun postShow() {
    }

    Route("/article/<id:int>")
    fun articleShow() {
    }

    Route("/article/images/<path:path>")
    fun articleImage() {
    }
}

class RouterTest {
    Test
    fun testExactMatch() {
        Assert.assertEquals("/", app.router.findHandler("/")?.route?.value)
        Assert.assertEquals(null, app.router.findHandler("/not-exist-url"))
    }

    Test
    fun testStringVariable() {
        val chain = app.router.findHandler("/post/post name")
        Assert.assertEquals("/post/<name>", chain?.route?.value)
        Assert.assertEquals("post name", chain?.parseResult?.pathVariables?.get("name"))
    }

    Test
    fun testPathVariable() {
        val handler = app.router.findHandler("/article/images/nested-path/image-name.jpg")
        Assert.assertEquals("nested-path/image-name.jpg", handler?.parseResult?.pathVariables?.get("path"))
    }
}

class RouterParseTest {
    Test
    fun testParse() {
        val parseResult = parse("/post/<name>", "/post/first post")
        Assert.assertEquals(ParseResult(mapOf("name" to "first post")), parseResult)
    }

    Test
    fun testCompile() {
        val rulePattern = compile("/post/<name>")
        Assert.assertEquals(
                "^/post/(?<name>[^/]+)$", rulePattern.pattern.pattern()
        )
        Assert.assertEquals(
                listOf("name"), rulePattern.groups.map { it.name }
        )
    }

    Test
    fun testMatch() {
        Assert.assertEquals(
                mapOf("name" to "post-name"),
                match(rulePattern = compile(rule = "/post/<name>"), uri = "/post/post-name")
        )
    }

    Test
    fun testInt() {
        Assert.assertEquals(
                ParseResult(mapOf("id" to 1234)), parse(rule = "/post/<id:int>", uri = "/post/1234")
        )
    }

    Test
    fun testPath() {
        Assert.assertEquals(
                ParseResult(mapOf("path" to "nested-path/image-name.jpg")), parse(rule = "/article/images/<path:path>", uri = "/article/images/nested-path/image-name.jpg")
        )
    }
}
