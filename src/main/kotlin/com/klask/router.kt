package com.klask.router

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import com.klask.KlaskApp
import java.lang.annotation.ElementType
import java.lang.annotation.Target
import com.klask.Response
import java.lang.invoke.WrongMethodTypeException
import com.klask.StringResponse

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

public class HandlerChain(val rule: String, val app: KlaskApp, val child: HandlerChain?, val invoke: () -> Response)