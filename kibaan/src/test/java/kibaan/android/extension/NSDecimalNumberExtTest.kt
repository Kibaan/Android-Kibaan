package kibaan.android.extension

import kibaan.android.ios.NSDecimalNumber
import org.junit.Assert.assertEquals
import org.junit.Test

class NSDecimalNumberExtTest {

    @Test
    fun testRoundDown() {
        val decimal = NSDecimalNumber(string = "1.98765")

        assertEquals("1.98765", decimal.stringValue)
        assertEquals("1", decimal.roundDown().stringValue)
        assertEquals("1.9", decimal.roundDown(1).stringValue)
        assertEquals("1.98", decimal.roundDown(2).stringValue)
        assertEquals("-1", NSDecimalNumber(string = "-0.1").roundDown().stringValue)
    }

    @Test
    fun testRoundUp() {
        val decimal = NSDecimalNumber(string = "1.12345")
        assertEquals("1.12345", decimal.stringValue)

        assertEquals("2", decimal.roundUp().stringValue)
        assertEquals("1.2", decimal.roundUp(1).stringValue)
        assertEquals("1.13", decimal.roundUp(2).stringValue)
        assertEquals("0", NSDecimalNumber(string = "-0.1").roundUp().stringValue)
    }

    @Test
    fun testRoundAbsDown() {
        assertEquals("0", NSDecimalNumber(string = "0.1").roundAbsDown().stringValue)
        assertEquals("1.9", NSDecimalNumber(string = "1.98").roundAbsDown(1).stringValue)
        assertEquals("0", NSDecimalNumber(string = "-0.1").roundAbsDown().stringValue)
        assertEquals("-1", NSDecimalNumber(string = "-1.9").roundAbsDown().stringValue)
    }

    @Test
    fun testRoundAbsUp() {
        assertEquals("1", NSDecimalNumber(string = "0.1").roundAbsUp().stringValue)
        assertEquals("2.0", NSDecimalNumber(string = "1.91").roundAbsUp(1).stringValue)
        assertEquals("-1", NSDecimalNumber(string = "-0.1").roundAbsUp().stringValue)
        assertEquals("-2", NSDecimalNumber(string = "-1.1").roundAbsUp().stringValue)
    }

    @Test
    fun testRoundPlain() {
        var decimal = NSDecimalNumber(string = "1.432")
        assertEquals("1", decimal.roundPlain().stringValue)
        assertEquals("1.4", decimal.roundPlain(1).stringValue)
        assertEquals("1.43", decimal.roundPlain(2).stringValue)

        assertEquals("1.432", decimal.stringValue)

        decimal = NSDecimalNumber(string = "1.567")
        assertEquals("2", decimal.roundPlain().stringValue)
        assertEquals("1.6", decimal.roundPlain(1).stringValue)
        assertEquals("1.57", decimal.roundPlain(2).stringValue)

        assertEquals("1.567", decimal.stringValue)

    }
}
