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
}

class RouterTest {
    Test
    fun testExactMatch() {
        Assert.assertEquals("/", app.router.findHandlerChain("/")?.rule)
        Assert.assertEquals(null, app.router.findHandlerChain("/not-exist-url"))
    }

    Test
    fun testStringPathVariable() {
        val chain = app.router.findHandlerChain("/post/first post")
        Assert.assertEquals("/post/<name>", chain?.rule)
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
        Assert.assertEquals(
                RulePattern(
                        pattern = Pattern.compile("^/post/(?<name>[^/]+)$"),
                        groups = listOf("name")
                ),
                compile("/post/<name>")
        )
    }

    Test
    fun testMatch() {
        Assert.assertEquals(
                mapOf("name" to "post-name"),
                match(
                        rulePattern = RulePattern(
                                pattern =  Pattern.compile("^/post/(?<name>[^/]+)$"),
                                groups = listOf("name")
                        ),
                        uri = "/post/post-name"
                )
        )
    }
}
