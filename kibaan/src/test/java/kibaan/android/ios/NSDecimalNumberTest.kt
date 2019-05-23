package kibaan.android.ios

import kibaan.android.extension.stringValue
import org.junit.Assert.*
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

        decimal = NSDecimalNumber(value = -123L)
        assertEquals(BigDecimal(-123L), decimal.bigDecimal)

        decimal = NSDecimalNumber(value = -123.45)
        assertEquals(BigDecimal.valueOf(-123.45), decimal.bigDecimal)

        decimal = NSDecimalNumber(bigDecimal = BigDecimal(-123.45))
        assertEquals(BigDecimal(-123.45), decimal.bigDecimal)

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
    fun testHashCode() {
        val one = NSDecimalNumber(1)
        val two = NSDecimalNumber("2")
        val three = NSDecimalNumber(3.2)
        assertEquals(BigDecimal(1).hashCode(), one.hashCode())
        assertEquals(BigDecimal("2").hashCode(), two.hashCode())
        assertEquals(BigDecimal.valueOf(3.2).hashCode(), three.hashCode())
    }

    @Test
    fun testArithmetic() {

        val one = NSDecimalNumber("1")
        val two = NSDecimalNumber("2")
        val three = NSDecimalNumber("3")
        val four = 4

        assertEquals("3", one.adding(two).stringValue)
        assertEquals("5", one.adding(four).stringValue)
        assertEquals("1", one.stringValue)
        assertEquals("2", two.stringValue)
        assertEquals("3", three.stringValue)

        assertEquals("-2", one.subtracting(three).stringValue)
        assertEquals("-3", one.subtracting(four).stringValue)
        assertEquals("1", one.stringValue)
        assertEquals("2", two.stringValue)
        assertEquals("3", three.stringValue)

        assertEquals("6", two.multiplying(three).stringValue)
        assertEquals("8", two.multiplying(four).stringValue)
        assertEquals("1", one.stringValue)
        assertEquals("2", two.stringValue)
        assertEquals("3", three.stringValue)

        assertEquals("1.5", three.dividing(two).stringValue)
        assertEquals("0.5", two.dividing(four).stringValue)
        assertEquals("1", one.stringValue)
        assertEquals("2", two.stringValue)
        assertEquals("3", three.stringValue)

        assertEquals("1", one.raising(four).stringValue)
        assertEquals("16", two.raising(four).stringValue)
        assertEquals("81", three.raising(four).stringValue)

        // doubleValueのテスト
        assertTrue(one.doubleValue == 1.0)
        assertTrue(NSDecimalNumber(0.54).doubleValue == 0.54)
        assertTrue(NSDecimalNumber.notANumber.doubleValue.stringValue == Double.NaN.stringValue)
    }

    @Test
    fun testStatic() {
        assertEquals("0", NSDecimalNumber.zero.stringValue)
        assertEquals("1", NSDecimalNumber.one.stringValue)
        assertEquals(Long.MAX_VALUE.stringValue, NSDecimalNumber.maximum.stringValue)
        assertEquals(Long.MIN_VALUE.stringValue, NSDecimalNumber.minimum.stringValue)
    }

    @Test
    fun testCompare() {
        assertEquals(ComparisonResult.orderedAscending, NSDecimalNumber.zero.compare(NSDecimalNumber.one))
        assertEquals(ComparisonResult.orderedDescending, NSDecimalNumber.one.compare(NSDecimalNumber.zero))
        assertEquals(ComparisonResult.orderedSame, NSDecimalNumber.zero.compare(NSDecimalNumber.zero))
    }

    @Test
    fun testCompareTo() {
        assertEquals(-1, NSDecimalNumber.zero.compareTo(NSDecimalNumber.one))
        assertEquals(1, NSDecimalNumber.one.compareTo(NSDecimalNumber.zero))
        assertEquals(0, NSDecimalNumber.zero.compareTo(NSDecimalNumber.zero))
        assertEquals(0, NSDecimalNumber.one.compareTo(NSDecimalNumber.one))

        assertFalse(NSDecimalNumber.zero == (NSDecimalNumber.one))
        assertTrue(NSDecimalNumber.zero != (NSDecimalNumber.one))
        assertTrue(NSDecimalNumber.zero < (NSDecimalNumber.one))
        assertTrue(NSDecimalNumber.zero <= (NSDecimalNumber.one))
        assertFalse(NSDecimalNumber.zero > (NSDecimalNumber.one))
        assertFalse(NSDecimalNumber.zero >= (NSDecimalNumber.one))
    }

    @Test
    fun testDivide() {
        assertEquals("0.33333333333333333333333333333333333333", NSDecimalNumber("1").dividing(NSDecimalNumber("3")).stringValue)
    }

}
