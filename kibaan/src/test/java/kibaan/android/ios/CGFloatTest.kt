package kibaan.android.ios

import org.junit.Assert
import org.junit.Test

class CGFloatTest {

    @Test
    fun testDescription() {
        var f = 123.0
        Assert.assertTrue(f.description.startsWith("123.0"))
        f = -123.45
        Assert.assertTrue(f.description.startsWith("-123.45"))
        f = CGFloat.nan
        Assert.assertEquals("NaN", f.description)
    }

    @Test
    fun testLeastNormalMagnitude() {
        Assert.assertTrue(CGFloat.leastNormalMagnitude == Double.MIN_VALUE)
    }

    @Test
    fun testNan() {
        Assert.assertTrue(CGFloat.nan.description == Double.NaN.description)
    }

    @Test
    fun testIsNaN() {
        val value: CGFloat = CGFloat.nan
        Assert.assertEquals(true, value.isNaN)
    }

    @Test
    fun testIsNotNaN() {
        val value = 2.3
        Assert.assertEquals(false, value.isNaN)
    }

    @Test
    fun testInvokeInt() {
        val value: Int = 1
        Assert.assertEquals(1.0, CGFloat(value), 1.0)
    }

    @Test
    fun testInvokeLong() {
        val value: Long = 1
        Assert.assertEquals(1.0, CGFloat(value), 1.0)
    }

    @Test
    fun testInvokeFloat() {
        val value: Float = 1.0f
        Assert.assertEquals(1.0, CGFloat(value), 1.0)
    }

    @Test
    fun testInvokeDouble() {
        val value: Double = 1.0
        Assert.assertEquals(1.0, CGFloat(value), 1.0)
    }
}