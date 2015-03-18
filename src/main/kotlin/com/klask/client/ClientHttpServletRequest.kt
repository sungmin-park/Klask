package com.klask.client

import javax.servlet.http.HttpServletRequest
import java.net.URL
import java.util.Enumeration
import javax.servlet.ServletInputStream
import java.io.BufferedReader
import java.util.Locale
import javax.servlet.RequestDispatcher
import javax.servlet.ServletContext
import javax.servlet.AsyncContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.DispatcherType
import javax.servlet.http.Cookie
import java.security.Principal
import javax.servlet.http.HttpSession
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.Part
import javax.servlet.http.HttpUpgradeHandler

class ClientHttpServletRequest(val requestUrl: String) : HttpServletRequest {
    val url: URL
    {
        url = URL(if (requestUrl.startsWith("http://")) "" else "http://localhost:5000")
    }

    override fun getAttribute(name: String?): Any? {
        throw UnsupportedOperationException()
    }

    override fun getAttributeNames(): Enumeration<String>? {
        throw UnsupportedOperationException()
    }

    override fun getCharacterEncoding(): String? {
        throw UnsupportedOperationException()
    }

    override fun setCharacterEncoding(env: String?) {
        throw UnsupportedOperationException()
    }

    override fun getContentLength(): Int {
        throw UnsupportedOperationException()
    }

    override fun getContentLengthLong(): Long {
        throw UnsupportedOperationException()
    }

    override fun getContentType(): String? {
        throw UnsupportedOperationException()
    }

    override fun getInputStream(): ServletInputStream? {
        throw UnsupportedOperationException()
    }

    override fun getParameter(name: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun getParameterNames(): Enumeration<String>? {
        throw UnsupportedOperationException()
    }

    override fun getParameterValues(name: String?): Array<out String>? {
        throw UnsupportedOperationException()
    }

    override fun getParameterMap(): MutableMap<String, Array<String>>? {
        throw UnsupportedOperationException()
    }

    override fun getProtocol(): String? {
        throw UnsupportedOperationException()
    }

    override fun getScheme(): String? {
        throw UnsupportedOperationException()
    }

    override fun getServerName(): String? {
        throw UnsupportedOperationException()
    }

    override fun getServerPort(): Int {
        throw UnsupportedOperationException()
    }

    override fun getReader(): BufferedReader? {
        throw UnsupportedOperationException()
    }

    override fun getRemoteAddr(): String? {
        throw UnsupportedOperationException()
    }

    override fun getRemoteHost(): String? {
        throw UnsupportedOperationException()
    }

    override fun setAttribute(name: String?, o: Any?) {
        throw UnsupportedOperationException()
    }

    override fun removeAttribute(name: String?) {
        throw UnsupportedOperationException()
    }

    override fun getLocale(): Locale? {
        throw UnsupportedOperationException()
    }

    override fun getLocales(): Enumeration<Locale>? {
        throw UnsupportedOperationException()
    }

    override fun isSecure(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getRequestDispatcher(path: String?): RequestDispatcher? {
        throw UnsupportedOperationException()
    }

    override fun getRealPath(path: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun getRemotePort(): Int {
        throw UnsupportedOperationException()
    }

    override fun getLocalName(): String? {
        throw UnsupportedOperationException()
    }

    override fun getLocalAddr(): String? {
        throw UnsupportedOperationException()
    }

    override fun getLocalPort(): Int {
        throw UnsupportedOperationException()
    }

    override fun getServletContext(): ServletContext? {
        throw UnsupportedOperationException()
    }

    override fun startAsync(): AsyncContext? {
        throw UnsupportedOperationException()
    }

    override fun startAsync(servletRequest: ServletRequest?, servletResponse: ServletResponse?): AsyncContext? {
        throw UnsupportedOperationException()
    }

    override fun isAsyncStarted(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isAsyncSupported(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getAsyncContext(): AsyncContext? {
        throw UnsupportedOperationException()
    }

    override fun getDispatcherType(): DispatcherType? {
        throw UnsupportedOperationException()
    }

    override fun getAuthType(): String? {
        throw UnsupportedOperationException()
    }

    override fun getCookies(): Array<out Cookie>? {
        throw UnsupportedOperationException()
    }

    override fun getDateHeader(name: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun getHeader(name: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun getHeaders(name: String?): Enumeration<String>? {
        throw UnsupportedOperationException()
    }

    override fun getHeaderNames(): Enumeration<String>? {
        throw UnsupportedOperationException()
    }

    override fun getIntHeader(name: String?): Int {
        throw UnsupportedOperationException()
    }

    override fun getMethod(): String? {
        throw UnsupportedOperationException()
    }

    override fun getPathInfo(): String? {
        throw UnsupportedOperationException()
    }

    override fun getPathTranslated(): String? {
        throw UnsupportedOperationException()
    }

    override fun getContextPath(): String? {
        throw UnsupportedOperationException()
    }

    override fun getQueryString(): String? {
        throw UnsupportedOperationException()
    }

    override fun getRemoteUser(): String? {
        throw UnsupportedOperationException()
    }

    override fun isUserInRole(role: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getUserPrincipal(): Principal? {
        throw UnsupportedOperationException()
    }

    override fun getRequestedSessionId(): String? {
        throw UnsupportedOperationException()
    }

    override fun getRequestURI(): String? {
        val file = url.getFile()
        return if (file == "") "/" else file
    }

    override fun getRequestURL(): StringBuffer? {
        throw UnsupportedOperationException()
    }

    override fun getServletPath(): String? {
        throw UnsupportedOperationException()
    }

    override fun getSession(create: Boolean): HttpSession? {
        throw UnsupportedOperationException()
    }

    override fun getSession(): HttpSession? {
        throw UnsupportedOperationException()
    }

    override fun changeSessionId(): String? {
        throw UnsupportedOperationException()
    }

    override fun isRequestedSessionIdValid(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isRequestedSessionIdFromCookie(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isRequestedSessionIdFromURL(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isRequestedSessionIdFromUrl(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun authenticate(response: HttpServletResponse?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun login(username: String?, password: String?) {
        throw UnsupportedOperationException()
    }

    override fun logout() {
        throw UnsupportedOperationException()
    }

    override fun getParts(): MutableCollection<Part>? {
        throw UnsupportedOperationException()
    }

    override fun getPart(name: String?): Part? {
        throw UnsupportedOperationException()
    }

    override fun <T : HttpUpgradeHandler?> upgrade(handlerClass: Class<T>?): T {
        throw UnsupportedOperationException()
    }
}