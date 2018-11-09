package jp.co.altonotes.android.kibaan.ios

import org.junit.Assert.assertEquals
import org.junit.Test

class UIColorTest {

    @Test
    fun testColorCode() {
        assertEquals("FF0000", UIColor.red.colorCode)
        assertEquals("556677", UIColor(rgbValue = 0x556677).colorCode)
    }

    @Test
    fun testIntValue() {
        assertEquals(0xFF000000.toInt(), UIColor.black.intValue)
        assertEquals(0xFFFF0000.toInt(), UIColor.red.intValue)
        assertEquals(0xFF00FF00.toInt(), UIColor.green.intValue)
        assertEquals(0xFF0000FF.toInt(), UIColor.blue.intValue)
    }
}
