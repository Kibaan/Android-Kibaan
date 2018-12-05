package kibaan.extension

import kibaan.ios.CGFloat
import kibaan.ios.nan
import org.junit.Assert.assertEquals
import org.junit.Test

class CGFloatTest {

    @Test
    fun testString() {
        val f = 123.45
        assertEquals("123", f.stringValue(0))
        assertEquals("NaN", CGFloat.nan.stringValue(0))

        assert(f.stringValue.startsWith("123.45"))
    }

}
