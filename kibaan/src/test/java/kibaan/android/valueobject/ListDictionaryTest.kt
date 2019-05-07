package kibaan.android.valueobject

import org.junit.Assert
import org.junit.Test

class ListDictionaryTest {

    @Test
    fun testKeys() {
        val dict = ListDictionary(mapOf("1" to "AAA", "2" to "BBB", "3" to "CCC"))
        Assert.assertArrayEquals(arrayOf("1", "2", "3"), dict.keys.toTypedArray())
    }

    @Test
    fun testKeyAccess() {
        val dict = ListDictionary(mapOf("1" to "AAA", "2" to "BBB", "3" to "CCC"))
        Assert.assertEquals("AAA", dict["1"])
        Assert.assertEquals("BBB", dict["2"])
        Assert.assertEquals("CCC", dict["3"])
    }

    @Test
    fun testInvalidKeyAccess() {
        val dict = ListDictionary(mapOf("1" to "AAA", "2" to "BBB", "3" to "CCC"))
        Assert.assertNull(dict["4"])
    }

    @Test
    fun testIndexAccess() {
        val dict = ListDictionary(mapOf("1" to "AAA", "2" to "BBB", "3" to "CCC"))
        Assert.assertEquals("AAA", dict[0])
        Assert.assertEquals("BBB", dict[1])
        Assert.assertEquals("CCC", dict[2])
    }

    @Test
    fun testInvalidIndexAccess() {
        val dict = ListDictionary(mapOf("1" to "AAA", "2" to "BBB", "3" to "CCC"))
        Assert.assertNull(dict[3])
        Assert.assertNull(dict[-1])
    }

    @Test
    fun testSetNil() {
        val dict = ListDictionary(mapOf("1" to "AAA", "2" to "BBB", "3" to "CCC"))
        dict["2"] = null
        Assert.assertNull(dict["2"])
    }

    @Test
    fun testAppend() {
        val dict = ListDictionary(mapOf("1" to "AAA", "2" to "BBB", "3" to "CCC"))
        dict.append(key = "4", value = "DDD")
        Assert.assertEquals(listOf("1", "2", "3", "4"), dict.keys)
        Assert.assertEquals("AAA", dict["1"])
        Assert.assertEquals("BBB", dict["2"])
        Assert.assertEquals("CCC", dict["3"])
        Assert.assertEquals("DDD", dict["4"])
    }

    @Test
    fun testAppendSameKey() {
        val dict = ListDictionary(mapOf("1" to "AAA", "2" to "BBB", "3" to "CCC"))
        dict.append(key = "2", value = "DDD")
        Assert.assertEquals(listOf("1", "2", "3"), dict.keys)
        Assert.assertEquals("AAA", dict["1"])
        Assert.assertEquals("DDD", dict["2"])
        Assert.assertEquals("CCC", dict["3"])
    }
}
