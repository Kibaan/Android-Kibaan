package kibaan.android.http

import kibaan.android.framework.SingletonContainer
import kibaan.android.ios.append
import kibaan.android.ios.removeAll
import okhttp3.Cookie
import okhttp3.HttpUrl

class HTTPCookieStorage {

    // region -> Shared

    companion object {
        val shared: HTTPCookieStorage get() = SingletonContainer.get(HTTPCookieStorage::class)
    }

    // endregion

    // region -> Variables

    private var cookieMap: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    // endregion

    // region -> Function

    fun setCookies(url: HttpUrl, cookies: MutableList<Cookie>) {
        val targetList = cookieMap[url.host()] ?: mutableListOf()
        cookies.forEach { cookie ->
            targetList.removeAll { it.keyEquals(cookie) }
        }
        targetList.append(contentsOf = cookies)
        cookieMap[url.host()] = targetList
    }

    fun getCookies(url: HttpUrl): List<Cookie> {
        val host = url.host()
        removeExpiredCookies(host)

        val path = url.encodedPath()
        val targetList = cookieMap[host] ?: return mutableListOf()

        return targetList.filter {
            path.startsWith(it.path()) && it.domain() == host
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun removeExpiredCookies(host: String) {
        val targetList = cookieMap[host] ?: return
        targetList.removeAll { it.hasExpired }
        cookieMap[host] = targetList
    }

    fun clear() {
        cookieMap.removeAll()
    }

    // endregion
}

// region -> Cookie Extension

val Cookie.hasExpired: Boolean
    get() {
        return expiresAt() < System.currentTimeMillis()
    }

fun Cookie.keyEquals(other: Any?): Boolean {
    if (other !is Cookie) return false
    val that = other as Cookie?
    return that?.name() == name() && that?.domain() == domain() && that?.path() == path()
}

// endregion