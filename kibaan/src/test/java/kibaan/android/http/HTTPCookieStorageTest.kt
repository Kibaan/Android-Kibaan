package kibaan.android.http

import okhttp3.Cookie
import okhttp3.HttpUrl
import org.junit.Assert
import org.junit.Test

@Suppress("SpellCheckingInspection")
class HTTPCookieStorageTest {

    // 単純に保存および読み込みが可能であることを確認
    @Test
    fun testSetSingleCookie() {
        HTTPCookieStorage.shared.clear()
        val host = "google.co.jp"
        val path = "/test"
        val name = "CookieName"
        val value = "14021fdakfd0142jfdsl41"
        val expiresAt = System.currentTimeMillis() + 100
        val url = createHttpUrl(host, path)
        val cookie = createCookie(host, path, name, value, expiresAt)
        HTTPCookieStorage.shared.setCookies(url, cookies = mutableListOf(cookie))

        val savedCookies = HTTPCookieStorage.shared.getCookies(url)
        Assert.assertEquals(1, savedCookies.size)

        val savedCookie = savedCookies[0]
        Assert.assertEquals(host, savedCookie.domain())
        Assert.assertEquals(path, savedCookie.path())
        Assert.assertEquals(name, savedCookie.name())
        Assert.assertEquals(value, savedCookie.value())
        Assert.assertEquals(expiresAt, savedCookie.expiresAt())
    }

    // 上書きされることを確認
    @Test
    fun testSetCookieOverwrite() {
        HTTPCookieStorage.shared.clear()
        val host = "google.co.jp"
        val path = "/test"
        val name = "CookieName"
        val oldValue = "14021fdakfd0142jfdsl41"
        val oldExpiresAt = System.currentTimeMillis() + 100
        val url = createHttpUrl(host, path)
        val cookie = createCookie(host, path, name, oldValue, oldExpiresAt)
        HTTPCookieStorage.shared.setCookies(url, cookies = mutableListOf(cookie))

        val newValue = "fasdj042309jfds0ajfas0412"
        val newExpiresAt = System.currentTimeMillis() + 110
        val newCookie = createCookie(host, path, name, newValue, newExpiresAt)
        HTTPCookieStorage.shared.setCookies(url, cookies = mutableListOf(newCookie))

        val savedCookies = HTTPCookieStorage.shared.getCookies(url)
        Assert.assertEquals(1, savedCookies.size)

        val savedCookie = savedCookies[0]
        Assert.assertEquals(host, savedCookie.domain())
        Assert.assertEquals(path, savedCookie.path())
        Assert.assertEquals(name, savedCookie.name())
        Assert.assertEquals(newValue, savedCookie.value())
        Assert.assertEquals(newExpiresAt, savedCookie.expiresAt())
    }

    // 同一ドメインに複数のクッキーが保存出来ることを確認
    @Test
    fun testSetMultipleCookies() {
        HTTPCookieStorage.shared.clear()
        val host = "google.co.jp"
        val path = "/test"
        val value = "14021fdakfd0142jfdsl41"
        val expiresAt = System.currentTimeMillis() + 100
        val url = createHttpUrl(host, path)
        val cookieNames = listOf("CookiesName1", "CookieName2")

        HTTPCookieStorage.shared.setCookies(url, cookies = cookieNames.map { name ->
            createCookie(host, path, name, value, expiresAt)
        }.toMutableList())

        val savedCookies = HTTPCookieStorage.shared.getCookies(url)
        Assert.assertEquals(2, savedCookies.size)
        savedCookies.forEachIndexed { index, cookie ->
            Assert.assertEquals(host, cookie.domain())
            Assert.assertEquals(path, cookie.path())
            Assert.assertEquals(cookieNames[index], cookie.name())
            Assert.assertEquals(value, cookie.value())
            Assert.assertEquals(expiresAt, cookie.expiresAt())
        }
    }

    // 複数ドメインに複数のクッキーが保存出来ることを確認
    @Test
    fun testSetDifferentHostCookies() {
        HTTPCookieStorage.shared.clear()
        val host = "google.co.jp"
        val path = "/test"
        val value = "14021fdakfd0142jfdsl41"
        val expiresAt = System.currentTimeMillis() + 100
        val url = createHttpUrl(host, path)
        val cookieNames = listOf("CookiesName1", "CookieName2")

        HTTPCookieStorage.shared.setCookies(url, cookies = cookieNames.map { name ->
            createCookie(host, path, name, value, expiresAt)
        }.toMutableList())

        val host2 = "yahoo.co.jp"
        val path2 = "/test"
        val value2 = "0142u0fjdas09u4092j0fasdz"
        val expiresAt2 = System.currentTimeMillis() + 100
        val url2 = createHttpUrl(host2, path2)
        val cookieNames2 = listOf("CookiesName1", "CookieName2")

        HTTPCookieStorage.shared.setCookies(url2, cookies = cookieNames2.map { name ->
            createCookie(host2, path2, name, value2, expiresAt2)
        }.toMutableList())

        val savedCookies = HTTPCookieStorage.shared.getCookies(url)
        Assert.assertEquals(2, savedCookies.size)
        savedCookies.forEachIndexed { index, cookie ->
            Assert.assertEquals(host, cookie.domain())
            Assert.assertEquals(path, cookie.path())
            Assert.assertEquals(cookieNames[index], cookie.name())
            Assert.assertEquals(value, cookie.value())
            Assert.assertEquals(expiresAt, cookie.expiresAt())
        }

        val savedCookies2 = HTTPCookieStorage.shared.getCookies(url2)
        Assert.assertEquals(2, savedCookies2.size)
        savedCookies2.forEachIndexed { index, cookie ->
            Assert.assertEquals(host2, cookie.domain())
            Assert.assertEquals(path2, cookie.path())
            Assert.assertEquals(cookieNames2[index], cookie.name())
            Assert.assertEquals(value2, cookie.value())
            Assert.assertEquals(expiresAt2, cookie.expiresAt())
        }
    }

    // Pathの判定確認(完全一致,前方一致,一致していない)
    @Test
    fun testPathJudge() {
        HTTPCookieStorage.shared.clear()
        val host = "google.co.jp"
        val value = "14021fdakfd0142jfdsl41"
        val expiresAt = System.currentTimeMillis() + 100
        val url = createHttpUrl(host, "/test")
        val cookieNames = listOf("CookiesName1", "CookieName2")

        HTTPCookieStorage.shared.setCookies(url, cookies = cookieNames.map { name ->
            createCookie(host, "/test", name, value, expiresAt)
        }.toMutableList())

        // 完全一致
        Assert.assertEquals(2, HTTPCookieStorage.shared.getCookies(createHttpUrl(host, path = "/test")).size)

        // 前方一致
        Assert.assertEquals(2, HTTPCookieStorage.shared.getCookies(createHttpUrl(host, path = "/test/test2")).size)

        // 一致していない
        Assert.assertEquals(0, HTTPCookieStorage.shared.getCookies(createHttpUrl(host, path = "/yahoo")).size)
    }

    // 有効期限の判定確認
    @Test
    fun testHasExpired() {
        HTTPCookieStorage.shared.clear()
        val host = "google.co.jp"
        val value = "14021fdakfd0142jfdsl41"
        val expiresAt = System.currentTimeMillis() + 100
        val url = createHttpUrl(host, "/test")
        val cookieNames = listOf("CookiesName1", "CookieName2")

        HTTPCookieStorage.shared.setCookies(url, cookies = cookieNames.map { name ->
            createCookie(host, "/", name, value, expiresAt)
        }.toMutableList())

        // 有効期限切れていない
        Assert.assertEquals(2, HTTPCookieStorage.shared.getCookies(createHttpUrl(host, path = "/")).size)

        // 有効期限まだ切れていない
        Thread.sleep(50)
        Assert.assertEquals(2, HTTPCookieStorage.shared.getCookies(createHttpUrl(host, path = "/")).size)

        // 有効期限切れた
        Thread.sleep(100)
        Assert.assertEquals(0, HTTPCookieStorage.shared.getCookies(createHttpUrl(host, path = "/")).size)
    }

    // region -> Support

    private fun createHttpUrl(host: String, path: String): HttpUrl {
        return HttpUrl.Builder().scheme("https").host(host).encodedPath(path).build()
    }

    private fun createCookie(host: String, path: String, name: String, value: String, expiresAt: Long): Cookie {
        return Cookie.Builder().domain(host).path(path).name(name).value(value).expiresAt(expiresAt).build()
    }

    // endregion
}
