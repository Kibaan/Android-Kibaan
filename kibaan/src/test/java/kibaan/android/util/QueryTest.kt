package kibaan.android.util

import kibaan.android.valueobject.KeyValue
import org.junit.Assert.assertEquals
import org.junit.Test

class QueryTest {

    @Test
    fun testParseSingle() {
        val query = Query(string = "key=value")
        assertEquals(1, query.keyValues.size)
        assertEquals("key=value", query.keyValues.firstOrNull()?.stringValue)
    }

    @Test
    fun testParse() {
        val query = Query(string = "aaa=111&bbb=222&ccc=333")
        val keyValues = query.keyValues
        assertEquals(3, keyValues.size)
        assertEquals("aaa=111", keyValues[0].stringValue)
        assertEquals("bbb=222", keyValues[1].stringValue)
        assertEquals("ccc=333", keyValues[2].stringValue)
    }

    @Test
    fun testParseNone() {
        val query = Query(string = "")
        assertEquals(0, query.keyValues.size)
    }

    @Test
    fun testURLDecode() {
        val query = Query(string = "aaa=111&bbb=%e3%81%82%e3%81%84%e3%81%86%26&ccc=333")
        val keyValues = query.keyValues
        assertEquals(3, keyValues.size)
        assertEquals("aaa=111", keyValues[0].stringValue)
        assertEquals("bbb=あいう&", keyValues[1].stringValue)
        assertEquals("ccc=333", keyValues[2].stringValue)
    }

    @Test
    fun testString() {
        val srcKeyValues = listOf(KeyValue(key = "aaa", value = "111"), KeyValue(key = "bbb", value = "222"), KeyValue(key = "ccc", value = "333"))
        val query = Query(keyValues = srcKeyValues)
        assertEquals("aaa=111&bbb=222&ccc=333", query.stringValue)
    }

    @Test
    fun testURLEncode() {
        val srcKeyValues = listOf(KeyValue(key = "aaa", value = "111"), KeyValue(key = "bbb", value = "あいう&"), KeyValue(key = "ccc", value = "333"))
        val query = Query(keyValues = srcKeyValues)
        assertEquals("aaa=111&bbb=%E3%81%82%E3%81%84%E3%81%86%26&ccc=333", query.stringValue)
    }

    @Test
    fun testSubscriptGet() {
        val query = Query(string = "aaa=111&bbb=222&ccc=333")
        assertEquals("111", query["aaa"])
        assertEquals("222", query["bbb"])
        assertEquals("333", query["ccc"])
    }

    @Test
    fun testSubscriptGetURLDecode() {
        val query = Query(string = "aaa=111&bbb=%E3%81%82%E3%81%84%E3%81%86%26&ccc=333")
        assertEquals("111", query["aaa"])
        assertEquals("あいう&", query["bbb"])
        assertEquals("333", query["ccc"])
    }

    @Test
    fun testSubscriptSet() {
        val query = Query()
        query["aaa"] = "111"
        query["bbb"] = "222"
        assertEquals("aaa=111&bbb=222", query.stringValue)
    }

    @Test
    fun testSubscriptSetURLEncode() {
        val query = Query()
        query["aaa"] = "あいう&"
        assertEquals("aaa=%E3%81%82%E3%81%84%E3%81%86%26", query.stringValue)
    }

    @Test
    fun testTrim() {
        val query = Query(string = "   aaa=111&bbb=222&ccc=333   ")
        assertEquals("111", query["aaa"])
        assertEquals("222", query["bbb"])
        assertEquals("333", query["ccc"])
    }

}
