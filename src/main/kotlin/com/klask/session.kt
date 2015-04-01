package com.klask.session

import java.io.Serializable
import java.util.HashMap

private class SessionImpl : HashMap<String, Serializable?>()

object session {
    private val local = ThreadLocal<SessionImpl>()
    private val impl: SessionImpl
        get() = local.get()

    public fun get(key: String): Serializable? {
        return impl[key]
    }

    public fun set(key: String, value: Serializable?) {
        impl[key] = value
    }

    public fun remove(key: String) {
        impl.remove(key)
    }
}
