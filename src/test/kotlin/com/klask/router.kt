package com.klask

import org.junit.Test
import com.klask.router.Route
import org.junit.Assert
import com.klask.router.parse
import com.klask.router.ParseResult
import com.klask.router.compile
import java.util.regex.Pattern
import com.klask.router.match
import com.klask.router.RulePattern

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
}

class RouterTest {
    Test
    fun testExactMatch() {
        Assert.assertEquals("/", app.router.findHandler("/")?.route?.value)
        Assert.assertEquals(null, app.router.findHandler("/not-exist-url"))
    }

    Test
    fun testStringPathVariable() {
        val chain = app.router.findHandler("/post/post name")
        Assert.assertEquals("/post/<name>", chain?.route?.value)
        Assert.assertEquals("post name", chain?.parseResult?.pathVariables?.get("name"))
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
}
