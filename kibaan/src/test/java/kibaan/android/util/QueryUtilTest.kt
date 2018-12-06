package kibaan.android.util

import kibaan.android.valueobject.KeyValue
import org.junit.Assert.assertEquals
import org.junit.Test

class QueryUtilTest {

    @Test
    fun testOneParam() {
        val params = listOf<KeyValue>(KeyValue("aaa", "111"))
        val query = QueryUtils.makeQueryString(params)
        assertEquals("aaa=111", query)
    }

    @Test
    fun testMultiParam() {
        val params = listOf<KeyValue>(
            KeyValue("aaa", "111"),
            KeyValue("bbb", "222"),
            KeyValue("ccc", "333")
        )
        val query = QueryUtils.makeQueryString(params)
        assertEquals("aaa=111&bbb=222&ccc=333", query)
    }

    @Test
    fun testURLEncode() {
        val params = listOf<KeyValue>(
            KeyValue("aa=", "11&")
        )
        val query = QueryUtils.makeQueryString(params)
        assertEquals("aa%3D=11%26", query)
    }
}
