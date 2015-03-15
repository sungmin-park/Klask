package com.klask.router

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import com.klask.KlaskApp

Retention(RetentionPolicy.RUNTIME)
annotation class Route(val rule: String)

public class Router(val app: KlaskApp) {
    fun findHandler(requestURI: String): (() -> String)? {
        val handler = app.javaClass.getMethods()
                .map { it to it.getAnnotation(javaClass<Route>()) }
                .filter { it.second != null }
                .firstOrNull {
                    it.second.rule == requestURI
                }
        if (handler == null) {
            return null
        }
        return {
            handler.first.invoke(app) as String
        }
    }
}