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

        assertEquals(NSDecimalNumber.notANumber, NSDecimalNumber(string = "a"))
        assertEquals(NSDecimalNumber.notANumber, NSDecimalNumber(string = ""))
        assertEquals(NSDecimalNumber.notANumber, NSDecimalNumber(string = null))
        assertEquals(NSDecimalNumber.zero, NSDecimalNumber(string = "-"))
        assertEquals(NSDecimalNumber.zero, NSDecimalNumber(string = " -"))
        assertEquals(NSDecimalNumber.zero, NSDecimalNumber(string = "-0"))

        decimal = NSDecimalNumber.notANumber
        assertEquals(NSDecimalNumber.notANumber, decimal)

        assertEquals(ComparisonResult.orderedAscending, NSDecimalNumber.notANumber.compare(NSDecimalNumber.one))
        assertEquals(ComparisonResult.orderedAscending, NSDecimalNumber.notANumber.compare(NSDecimalNumber(-9999)))

        assertEquals(ComparisonResult.orderedDescending, NSDecimalNumber.zero.compare(NSDecimalNumber.notANumber))
        assertEquals(ComparisonResult.orderedDescending, NSDecimalNumber(-9999).compare(NSDecimalNumber.notANumber))
    }

    @Test
    fun testEqualBasic() {
        assertEquals(NSDecimalNumber(string = "100"), NSDecimalNumber(string = "100"))
        assertNotEquals(NSDecimalNumber(string = "100"), NSDecimalNumber(string = "101"))
    }

    @Test
    fun testEqualDifferentType() {
        // Double
        assertEquals(NSDecimalNumber(string = "0.3"), NSDecimalNumber(value = 0.3))
        assertNotEquals(NSDecimalNumber(string = "0.3"), NSDecimalNumber(value = 0.31))

        // Int
        assertEquals(NSDecimalNumber(string = "123"), NSDecimalNumber(123))
        assertNotEquals(NSDecimalNumber(string = "998"), NSDecimalNumber(999))

        // Long
        assertEquals(NSDecimalNumber(string = "123"), NSDecimalNumber(123L))
        assertNotEquals(NSDecimalNumber(string = "998"), NSDecimalNumber(999L))

        // BigDecimal
        assertEquals(NSDecimalNumber(string = "1"), NSDecimalNumber(BigDecimal.ONE))
        assertNotEquals(NSDecimalNumber(string = "1"), NSDecimalNumber(BigDecimal.ZERO))

    }

    @Test
    fun testEqualDecimal() {
        assertEquals(NSDecimalNumber(string = "1"), NSDecimalNumber(string = "1.00000"))
        assertEquals(NSDecimalNumber(string = "0.1"), NSDecimalNumber(string = "0.10"))
        assertEquals(NSDecimalNumber(string = "1E+1"), NSDecimalNumber(string = "10"))
    }

    @Test
    fun testEqualNan() {
        assertEquals(NSDecimalNumber.notANumber, NSDecimalNumber(Double.NaN))
        assertEquals(NSDecimalNumber.notANumber, NSDecimalNumber("あ"))
        assertEquals(NSDecimalNumber.notANumber, NSDecimalNumber(""))
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
    fun testStringValue() {
        assertEquals("1", NSDecimalNumber("1").stringValue)
        assertEquals("0.1", NSDecimalNumber("0.1").stringValue)
        assertEquals("0.001", NSDecimalNumber("0.001").stringValue)
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

        // doubleValueのテスト
        assertTrue(one.doubleValue == 1.0)
        assertTrue(NSDecimalNumber(0.54).doubleValue == 0.54)
        assertTrue(NSDecimalNumber.notANumber.doubleValue.stringValue == Double.NaN.stringValue)
    }

    @Test
    fun testRaising() {
        assertEquals("1", NSDecimalNumber("1").raising(4).stringValue)
        assertEquals("16", NSDecimalNumber("2").raising(4).stringValue)
        assertEquals("81", NSDecimalNumber("3").raising(4).stringValue)
    }

    @Test
    fun testMultiplyingByPowerOf10() {
        assertEquals("0.1", NSDecimalNumber("1").multiplyingByPowerOf10(-1).stringValue)
        assertEquals("1", NSDecimalNumber("1").multiplyingByPowerOf10(0).stringValue)
        assertEquals("10", NSDecimalNumber("1").multiplyingByPowerOf10(1).stringValue)
        assertEquals("100", NSDecimalNumber("1").multiplyingByPowerOf10(2).stringValue)
        assertEquals("2000", NSDecimalNumber("2").multiplyingByPowerOf10(3).stringValue)
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
