package com.klask.router

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import com.klask.KlaskApp
import java.lang.annotation.ElementType
import java.lang.annotation.Target
import javax.jws

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Route(val rule: String)

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.METHOD)
annotation class Routes(vararg val routes: Route)


public class Router(val app: KlaskApp) {
    val handlerChains: List<jws.HandlerChain>

    {
    }

    fun findHandlerChain(requestURI: String): HandlerChain? {
        val handler = app.javaClass.getMethods()
                .map { it to it.getAnnotation(javaClass<Route>()) }
                .filter { it.second != null }
                .firstOrNull {
                    it.second.rule == requestURI
                }
        if (handler == null) {
            return null
        }
        return HandlerChain(rule = handler.second.rule, parent = null)
    }
}

public class HandlerChain(val rule: String, val parent: HandlerChain?) {
    val children = arrayListOf<HandlerChain>()
}