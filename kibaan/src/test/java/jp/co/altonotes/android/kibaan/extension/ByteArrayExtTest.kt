package jp.co.altonotes.android.kibaan.extension

import jp.co.altonotes.android.kibaan.ios.data
import org.junit.Assert.assertEquals
import org.junit.Test

class ByteArrayExtTest {

    @Test
    fun testHexString_Text() {
        val testText = "テスト"
        val testData = testText.data(using = Charsets.UTF_8)
        assertEquals("E3 83 86 E3 82 B9 E3 83 88", testData.hexString)
    }

    @Test
    fun testHexString_Number() {
        val testText = "123456789"
        val testData = testText.data(using = Charsets.UTF_8)
        assertEquals("31 32 33 34 35 36 37 38 39", testData.hexString)
    }

    @Test
    fun testHexArrayUpper() {
        val testText = "テスト"
        val testData = testText.data(using = Charsets.UTF_8)
        assertEquals(listOf("E3", "83", "86", "E3", "82", "B9", "E3", "83", "88"), testData.hexArray(charCase = HexCase.upper))
    }

    @Test
    fun testHexArrayLower() {
        val testText = "テスト"
        val testData = testText.data(using = Charsets.UTF_8)
        assertEquals(listOf("e3", "83", "86", "e3", "82", "b9", "e3", "83", "88"), testData.hexArray(charCase = HexCase.lower))
    }
}
