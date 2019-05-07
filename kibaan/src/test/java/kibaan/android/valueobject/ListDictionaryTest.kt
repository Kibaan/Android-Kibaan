package kibaan.android.valueobject

import kibaan.android.ios.count
import org.junit.Assert
import org.junit.Test

class ListDictionaryTest {

    @Test
    fun testKeys() {
        val dict = ListDictionary(mapOf(
            "1" to Data("aaa"),
            "2" to Data("bbb"),
            "3" to Data("ccc")
        ))
        Assert.assertArrayEquals(arrayOf("1", "2", "3"), dict.keys.toTypedArray())
    }

    @Test
    fun testIndex() {
        val dict = ListDictionary(mapOf(
            "1" to Data("aaa"),
            "2" to Data("bbb"),
            "3" to Data("ccc")
        ))

        Assert.assertEquals("aaa", dict[0]?.text)
        Assert.assertEquals("bbb", dict[1]?.text)
        Assert.assertEquals("ccc", dict[2]?.text)
    }

    @Test
    fun testKeyAccess() {
        val dict = ListDictionary(mapOf(
            "1" to Data("aaa"),
            "2" to Data("bbb"),
            "3" to Data("ccc")
        ))

        Assert.assertEquals("aaa", dict["1"]?.text)
        Assert.assertEquals("bbb", dict["2"]?.text)
        Assert.assertEquals("ccc", dict["3"]?.text)
    }

    data class Data(val text: String)
}
