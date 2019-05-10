@file:Suppress("SpellCheckingInspection")

package kibaan.android.ios

import kibaan.android.extension.hexString
import org.junit.Assert.*
import org.junit.Test
import java.nio.charset.Charset

class StringExtTest {

    @Test
    fun testData() {
        val byteArray = "あい".data(Charset.forName("UTF-8"))
        assertEquals("E3 81 82 E3 81 84", byteArray.hexString)
    }

    @Test
    fun testComponents() {
        val list = "1,2,3".components(",")
        assertArrayEquals(arrayOf("1", "2", "3"), list.toTypedArray())
    }

    @Test
    fun testSplitWithoutEmpty() {
        val word = "東京海上  日本   "
        val words = word.splitWithoutEmpty(separator = " ")
        assertEquals(2, words.count)
        assertEquals("東京海上", words[0])
        assertEquals("日本", words[1])
    }

    @Test
    fun testPrefixSuffix() {
        val src = "PX_123456.SX"

        assertTrue(src.hasPrefix("PX_"))
        assertFalse(src.hasPrefix("RX_"))

        assertTrue(src.hasSuffix(".SX"))
        assertFalse(src.hasSuffix(".RX"))
    }

    @Test
    fun testRemoveLast() {
        assertEquals("AB", "ABC".removeLast())
        assertEquals("1.2", "1.23".removeLast())
    }

    @Test
    fun testRemoveLastSuffix() {
        assertEquals("1", "123".removeLast("23"))
    }

    @Test
    fun testReplacingOccurrences() {
        val src = "ABCD"
        assertEquals("A__D", src.replacingOccurrences("BC", "__"))
    }

    @Test
    fun testPrefix() {
        val src = "ABCD"
        assertEquals("A", src.prefix(1))
        assertEquals("AB", src.prefix(2))
    }

    @Test
    fun testLowercased() {
        val src = "ABCD"
        assertEquals("abcd", src.lowercased())
    }

    @Test
    fun testTrim() {
        val src = "  ABCD  "
        assertEquals("ABCD", src.trim(" "))
    }

    @Test
    fun testAppend() {
        val src = "ABC"
        assertEquals("ABCD", src.append("D"))
    }

    @Test
    fun testRemovePercentEncoding() {
        val src = "%e3%81%82%e3%81%84%e3%81%86"
        assertEquals("あいう", src.removingPercentEncoding)
    }

    @Test
    fun testDropLast() {
        val src = "ABC"
        assertEquals("AB", src.dropLast())
    }

    @Test
    fun testCount() {
        var src = "ABC"
        assertEquals(3, src.count)
        src = "123456789"
        assertEquals(9, src.count)
    }

    @Test
    fun tesRange() {
        val src = "ABC"
        val range = src.range("BC")

        assertNotNull(range)

        assertEquals(1, range?.first)
        assertEquals(3, range?.last)
    }

    @Test
    fun testHashValue() {
        var src = "ABC"
        assertEquals(src.hashCode(), src.hashValue)
        src = "DDD"
        assertEquals(src.hashCode(), src.hashValue)
    }

    @Test
    fun testApplyingTransform() {
        assertEquals("アカイオシャレ龍", "あかいオシャレ龍".applyingTransform(StringTransform.hiraganaToKatakana, reverse = false))
        assertEquals("あかいおしゃれ龍", "あかいオシャレ龍".applyingTransform(StringTransform.hiraganaToKatakana, reverse = true))
    }

}
