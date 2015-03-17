package com.klask.router

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import com.klask.KlaskApp
import java.lang.annotation.ElementType
import java.lang.annotation.Target
import com.klask.Response
import java.lang.invoke.WrongMethodTypeException
import com.klask.StringResponse
import java.util.regex.Pattern

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Route(val rule: String)

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Routes(vararg val routes: Route)


public class Router(val app: KlaskApp) {
    fun findHandlerChain(requestURI: String, urlPrefix: String = ""): HandlerChain? {
        val method = app.javaClass.getMethods()
                .map { it to it.getAnnotation(javaClass<Route>()) }
                .filter { it.second != null }
                .sortBy { it.second.rule }
                .firstOrNull {
                    requestURI == urlPrefix + it.second.rule
                }
        if (method != null) {
            return HandlerChain(app = app, rule = method.second.rule, child = null) {
                val res = method.first.invoke(app)
                when (res) {
                    is Response -> res
                    is String -> StringResponse(content = res)
                    else -> throw WrongMethodTypeException()
                }
            }
        }
        return null
    }
}

data class ParseResult(val pathVariables: Map<String, Any>)
data class RulePattern(val pattern: Pattern, val groups: List<String>) {
    override fun equals(other: Any?): Boolean {
        if (other !is RulePattern) {
            return false
        }
        return pattern.pattern() == other.pattern.pattern() && groups == other.groups
    }
}

fun parse(rule: String, uri: String): ParseResult? {
    val rulePattern = compile(rule)
    val matched = match(rulePattern = rulePattern, uri = uri)
    if (matched == null) {
        return null
    }
    return ParseResult(pathVariables = matched)
}

fun compile(rule: String): RulePattern {
    val groups = arrayListOf<String>()
    val patched = rule.replaceAll("<([^>]+)>") {
        groups.add(it.group(1))
        "(?<${it.group(1)}>[^/]+)"
    }
    return RulePattern(
            pattern = Pattern.compile("^$patched$"),
            groups = groups
    )
}

fun match(rulePattern: RulePattern, uri: String): Map<String, Any>? {
    val matcher = rulePattern.pattern.matcher(uri)
    if (!matcher.matches()) {
        return null
    }
    return rulePattern.groups
            .map {  it to matcher.group(it) }
            .toMap()
}

public class HandlerChain(val rule: String, val app: KlaskApp, val child: HandlerChain?, val invoke: () -> Response)