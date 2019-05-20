package kibaan.android.ios

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UIColorTest {

    @Test
    fun testColorCode() {
        assertEquals("FF0000", UIColor.red.colorCode)
        assertEquals("556677", UIColor(rgbValue = 0x556677).colorCode)
    }

    @Test
    fun testEquals() {
        assertEquals(UIColor.red, UIColor(rgbValue = 0xFF0000))
        assertNotEquals(UIColor.red, UIColor(rgbValue = 0xFE0000))
    }

    @Test
    fun testIntValue() {
        assertEquals(0xFF000000.toInt(), UIColor.black.intValue)
        assertEquals(0xFFFF0000.toInt(), UIColor.red.intValue)
        assertEquals(0xFF00FF00.toInt(), UIColor.green.intValue)
        assertEquals(0xFF0000FF.toInt(), UIColor.blue.intValue)
    }

    @Test
    fun testInitFromHexString() {
        val expected = UIColor(rgbValue = 0x1234EF)
        assertEquals(expected, UIColor(rgbHex = "#1234EF"))
        assertEquals(expected, UIColor(rgbHex = "1234EF"))
        assertEquals(expected, UIColor(argbHex = "#FF1234EF"))
        assertEquals(expected, UIColor(argbHex = "FF1234EF"))
    }
}
