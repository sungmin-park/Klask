package com.klask.router

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import com.klask.KlaskApp
import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.util.regex.Pattern
import java.lang.reflect.Method

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Route(val value: String)

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Routes(vararg val routes: Route)


public class Router(val app: KlaskApp) {
    fun findHandler(requestURI: String): Handler? {
        val methodPairs = app.javaClass.getMethods()
                .map { it to it.getAnnotation(javaClass<Route>()) }
                .filter { it.second != null }
        for ((method, route) in methodPairs) {
            val parseResult = parse(rule = route.value, uri = requestURI)
            if (parseResult != null) {
                return Handler(appChain = listOf(app), method = method, route = route, parseResult = parseResult)
            }
        }
        return null
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
                "string" to ::StringGroupHandler, "int" to ::IntGroupHandler
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

class StringGroupHandler(name: String) : GroupHandler(name = name) {
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
    return rulePattern.groups
            .map { it.name to it.translate(matcher.group(it.name)) }
            .toMap()
}

public data class Handler(val appChain: List<KlaskApp>, val method: Method, val route: Route, val parseResult: ParseResult?)
