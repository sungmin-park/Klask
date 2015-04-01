package com.klask.client

import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.Locale
import javax.servlet.ServletOutputStream
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import kotlin.properties.Delegates

class ClientHttpServletResponse : HttpServletResponse {
    var statusCode: Int by Delegates.notNull()
    var charset: String? = null
    var _contentType: String? = null
    var cookies = arrayListOf<Cookie>()

    override fun getCharacterEncoding(): String? {
        throw UnsupportedOperationException()
    }

    override fun getContentType(): String? {
        throw UnsupportedOperationException()
    }

    override fun getOutputStream(): ServletOutputStream? {
        throw UnsupportedOperationException()
    }

    override fun getWriter(): PrintWriter? {
        return object : PrintWriter(ByteArrayOutputStream()) {
        }
    }

    override fun setCharacterEncoding(charset: String?) {
        this.charset = charset
    }

    override fun setContentLength(len: Int) {
        throw UnsupportedOperationException()
    }

    override fun setContentLengthLong(len: Long) {
        throw UnsupportedOperationException()
    }

    override fun setContentType(type: String?) {
        _contentType = type
    }

    override fun setBufferSize(size: Int) {
        throw UnsupportedOperationException()
    }

    override fun getBufferSize(): Int {
        throw UnsupportedOperationException()
    }

    override fun flushBuffer() {
        throw UnsupportedOperationException()
    }

    override fun resetBuffer() {
        throw UnsupportedOperationException()
    }

    override fun isCommitted(): Boolean {
        return false;
    }

    override fun reset() {
        throw UnsupportedOperationException()
    }

    override fun setLocale(loc: Locale?) {
        throw UnsupportedOperationException()
    }

    override fun getLocale(): Locale? {
        throw UnsupportedOperationException()
    }

    override fun addCookie(cookie: Cookie?) {
        cookies.add(cookie)
    }

    override fun containsHeader(name: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun encodeURL(url: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun encodeRedirectURL(url: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun encodeUrl(url: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun encodeRedirectUrl(url: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun sendError(sc: Int, msg: String?) {
        throw UnsupportedOperationException()
    }

    override fun sendError(sc: Int) {
        setStatus(sc)
    }

    override fun sendRedirect(location: String?) {
        throw UnsupportedOperationException()
    }

    override fun setDateHeader(name: String?, date: Long) {
        throw UnsupportedOperationException()
    }

    override fun addDateHeader(name: String?, date: Long) {
        throw UnsupportedOperationException()
    }

    override fun setHeader(name: String?, value: String?) {
        throw UnsupportedOperationException()
    }

    override fun addHeader(name: String?, value: String?) {
        throw UnsupportedOperationException()
    }

    override fun setIntHeader(name: String?, value: Int) {
        throw UnsupportedOperationException()
    }

    override fun addIntHeader(name: String?, value: Int) {
        throw UnsupportedOperationException()
    }

    override fun setStatus(sc: Int) {
        statusCode = sc
    }

    override fun setStatus(sc: Int, sm: String?) {
        throw UnsupportedOperationException()
    }

    override fun getStatus(): Int {
        throw UnsupportedOperationException()
    }

    override fun getHeader(name: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun getHeaders(name: String?): MutableCollection<String>? {
        throw UnsupportedOperationException()
    }

    override fun getHeaderNames(): MutableCollection<String>? {
        throw UnsupportedOperationException()
    }
}