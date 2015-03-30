package com.klask.router

import com.klask.Application
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.regex.Pattern

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Route(val value: String)

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Routes(vararg val routes: Route)


public class Router(val app: Application) {
    val routeMethods: List<Pair<Method, Route>>
        get() = app.javaClass.getMethods()
                .map { it to it.getAnnotation(javaClass<Route>()) }
                .filter { it.second != null }

    fun findHandler(requestURI: String, urlPrefix: String = ""): Handler? {
        for ((method, route) in routeMethods) {
            val parseResult = parse(rule = urlPrefix + route.value, uri = requestURI)
            if (parseResult != null) {
                return Handler(appChain = arrayListOf(app), method = method, route = route, parseResult = parseResult)
            }
        }
        for (blueprintJar in app.blueprintJars) {
            val handler = blueprintJar.blueprint.router.findHandler(requestURI, urlPrefix + blueprintJar.urlPrefix)
            if (handler != null) {
                handler.appChain.add(app)
                return handler
            }
        }
        return null
    }

    fun findUrl(endpoint: String): String {
        if ("." in endpoint) {
            val (name, other) = endpoint.split("[.]", limit = 2)
            val blueprintJar = app.blueprintJars.first { it.blueprint.name == name }
            return blueprintJar.urlPrefix + blueprintJar.blueprint.router.findUrl(other)
        }
        return routeMethods
                .first {
                    it.first.getName().equals(endpoint)
                }
                .second.value
    }
}

data class ParseResult(val pathVariables: Map<String, Any>)
data class RulePattern(val pattern: Pattern, val groups: List<GroupHandler>) {
    override fun equals(other: Any?): Boolean {
        if (other !is RulePattern) {
            return false
        }
        return pattern.pattern() == other.pattern.pattern() && groups == other.groups
    }
}

abstract data class GroupHandler(val name: String) {
    abstract val pattern: String
    abstract fun translate(value: String): Any;

    companion object {
        val handlers = mapOf(
                "string" to ::StringGroupHandler, "int" to ::IntGroupHandler,
                "path" to ::PathHandler
        )

        fun invoke(group: String): GroupHandler {
            val groups = group.split(":", 2)
            val name = groups.first()
            val type = if (groups.size() == 2) groups[1] else "string"
            val handlerConstructor = handlers[type]
            if (handlerConstructor == null) {
                throw IllegalArgumentException(type)
            }
            return handlerConstructor(name)
        }
    }
}

open class StringGroupHandler(name: String) : GroupHandler(name = name) {
    override val pattern: String
        get() = "(?<${name}>[^/]+)"

    override fun translate(value: String): Any {
        return value
    }
}

class IntGroupHandler(name: String) : GroupHandler(name = name) {
    override val pattern: String
        get() = "(?<${name}>[0-9]|[1-9][0-9]+)"

    override fun translate(value: String): Any {
        return value.toInt()
    }
}

class PathHandler(name: String) : StringGroupHandler(name = name) {
    override val pattern: String
        get() = "(?<${name}>.+)"
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
    val groups = arrayListOf<GroupHandler>()
    val patched = rule.replaceAll("<([^>]+)>") {
        val groupHandler = GroupHandler(group = it.group(1))
        groups.add(groupHandler)
        groupHandler.pattern
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
    val map = linkedMapOf<String, Any>()
    rulePattern.groups.forEach {
        map.put(it.name, it.translate(matcher.group(it.name)))
    }
    return map
}

public data class Handler(val appChain: ArrayList<Application>, val method: Method, val route: Route, val parseResult: ParseResult?)
