package com.klask.urlfor

import com.klask.currentApp
import java.util.regex.Pattern

fun buildUrl(rule: String, values: Array<out Pair<String, Any>>): String {
    val residual = values.toArrayList()
    val url = rule.replaceAll("<([^>:]+)(:[^>]+)?>") {
        it.group(1)
        val pair = residual.first { i -> i.first == it.group(1) }
        residual.remove(pair)
        pair.second.toString()
    }
    return url
}


public fun urlfor(endpoint: String, vararg values: Pair<String, Any>): String {
    return buildUrl(currentApp.router.findUrl(endpoint), values)
}
