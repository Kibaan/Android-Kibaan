package kibaan.ios

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.math.BigDecimal

class NSDecimalNumberTest {

    @Test
    fun testConstruct() {

        var decimal = NSDecimalNumber(string = null)
        assertNotEquals(BigDecimal("0"), decimal.bigDecimal)

        decimal = NSDecimalNumber(string = "123")
        assertEquals(BigDecimal("123"), decimal.bigDecimal)

        decimal = NSDecimalNumber(value = -123)
        assertEquals(BigDecimal(-123), decimal.bigDecimal)

        decimal = NSDecimalNumber.notANumber
        assertEquals(NSDecimalNumber.notANumber, decimal)

        assertEquals(ComparisonResult.orderedAscending, NSDecimalNumber.notANumber.compare(NSDecimalNumber.one))
        assertEquals(ComparisonResult.orderedAscending, NSDecimalNumber.notANumber.compare(NSDecimalNumber(-9999)))

        assertEquals(ComparisonResult.orderedDescending, NSDecimalNumber.zero.compare(NSDecimalNumber.notANumber))
        assertEquals(ComparisonResult.orderedDescending, NSDecimalNumber(-9999).compare(NSDecimalNumber.notANumber))

    }

    @Test
    fun testEqual() {

        val decimalString = NSDecimalNumber(string = "0.3")
        val decimalDouble = NSDecimalNumber(value = 0.3)
        val decimalDouble2 = NSDecimalNumber(value = 0.31)

        assertEquals(decimalString, decimalDouble)

        assertNotEquals(decimalString, decimalDouble2)
    }

    @Test
    fun testArithmetic() {

        val one = NSDecimalNumber("1")
        val two = NSDecimalNumber("2")
        val three = NSDecimalNumber("3")

        assertEquals("3", one.adding(two).stringValue)
        assertEquals("1", one.stringValue)
        assertEquals("2", two.stringValue)
        assertEquals("3", three.stringValue)

        assertEquals("-2", one.subtracting(three).stringValue)
        assertEquals("1", one.stringValue)
        assertEquals("2", two.stringValue)
        assertEquals("3", three.stringValue)

        assertEquals("6", two.multiplying(three).stringValue)
        assertEquals("1", one.stringValue)
        assertEquals("2", two.stringValue)
        assertEquals("3", three.stringValue)

        assertEquals("1.5", three.dividing(two).stringValue)
        assertEquals("1", one.stringValue)
        assertEquals("2", two.stringValue)
        assertEquals("3", three.stringValue)

    }

    @Test
    fun testStatic() {
        assertEquals("0", NSDecimalNumber.zero.stringValue)
        assertEquals("1", NSDecimalNumber.one.stringValue)
    }

    @Test
    fun testCompare() {
        assertEquals(ComparisonResult.orderedAscending, NSDecimalNumber.zero.compare(NSDecimalNumber.one))
        assertEquals(ComparisonResult.orderedDescending, NSDecimalNumber.one.compare(NSDecimalNumber.zero))
        assertEquals(ComparisonResult.orderedSame, NSDecimalNumber.zero.compare(NSDecimalNumber.zero))
    }

}
