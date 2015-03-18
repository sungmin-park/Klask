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
import java.lang.reflect.Method

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Route(val rule: String)

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Routes(vararg val routes: Route)


public class Router(val app: KlaskApp) {
    fun findHandler(requestURI: String): Handler? {
        val methodPairs = app.javaClass.getMethods()
                .map { it to it.getAnnotation(javaClass<Route>()) }
                .filter { it.second != null }
        for ((method, route) in methodPairs) {
            val parseResult = parse(rule = route.rule, uri = requestURI)
            if (parseResult != null) {
                return Handler(appChain = listOf(app), method = method, route = route, parseResult = parseResult)
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

abstract data class GroupHandler(val name: String) {
    abstract fun translate(): Any;
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
            .map { it to matcher.group(it) }
            .toMap()
}

public data class Handler(val appChain: List<KlaskApp>, val method: Method, val route: Route, val parseResult: ParseResult?)
