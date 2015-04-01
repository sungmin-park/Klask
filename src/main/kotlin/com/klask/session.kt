package com.klask.sessions

import java.io.*
import java.util.Base64
import java.util.HashMap
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream
import javax.servlet.http.Cookie

trait Session {
    public fun get(key: String): Any?

    public fun set(key: String, value: Serializable?)

    public fun set(key: String, value: String?)

    public fun remove(key: String)
}

public class SessionImpl(cookie: Cookie?) : Session {
    val map: HashMap<String, Serializable?>;

    init {
        map = deserialize(cookie) ?: hashMapOf()
    }

    private fun deserialize(cookie: Cookie?): HashMap<String, Serializable?>? {
        if (cookie == null) {
            return null
        }
        ByteArrayInputStream(Base64.getUrlDecoder().decode(cookie.getValue())).use {
            InflaterInputStream(it).use {
                ObjectInputStream(it).use {
                    [suppress("UNCHECKED_CAST")]
                    return it.readObject() as HashMap<String, Serializable?>
                }
            }
        }
    }

    override fun set(key: String, value: String?) {
        set(key, value as Serializable?)
    }

    override fun set(key: String, value: Serializable?) {
        map.put(key, value)
    }

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun get(key: String): Any? {
        return map.get(key)
    }

    fun serialize(): String? {
        val compressed = ByteArrayOutputStream().use {
            DeflaterOutputStream(it).use {
                ObjectOutputStream(it).use {
                    it.writeObject(map)
                }
            }
            it
        }.toByteArray()
        return Base64.getUrlEncoder().encodeToString(compressed)!!
    }
}

