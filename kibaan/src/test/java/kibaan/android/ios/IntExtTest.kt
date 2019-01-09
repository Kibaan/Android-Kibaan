package kibaan.android.ios

import org.junit.Assert
import org.junit.Test

class IntExtTest {

    @Test
    fun testIntDescription() {
        var value = 123
        Assert.assertEquals("123", value.description)

        value = -123
        Assert.assertEquals("-123", value.description)

        value = 123.45.toInt()
        Assert.assertEquals("123", value.description)

        value = (-123.45).toInt()
        Assert.assertEquals("-123", value.description)
    }

    @Test
    fun testLongDescription() {
        var value: Long = 123
        Assert.assertEquals("123", value.description)

        value = (-123).toLong()
        Assert.assertEquals("-123", value.description)

        value = 123.45.toLong()
        Assert.assertEquals("123", value.description)

        value = (-123.45).toLong()
        Assert.assertEquals("-123", value.description)
    }

}