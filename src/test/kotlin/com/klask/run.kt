package com.klask.run

import com.klask.Klask
import com.klask.Route


object app : Klask() {
    Route("/")
    fun hello(): String {
        return "Hello world"
    }
}

fun main(args: Array<String>) {
    app.run()
}
