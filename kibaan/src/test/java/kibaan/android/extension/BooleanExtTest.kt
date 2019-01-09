package kibaan.android.extension

import org.junit.Assert.*
import org.junit.Test

class BooleanExtTest {

    @Test
    fun testOptionalTrue() {
        val bool: Boolean? = true
        assertTrue(bool.isTrue)
    }

    @Test
    fun testOptionalFalse() {
        val bool: Boolean? = false
        assertFalse(bool.isTrue)
    }

    @Test
    fun testOptionalNil() {
        val bool: Boolean? = null
        assertFalse(bool.isTrue)
    }

    @Test
    fun testToggled() {
        assertEquals(false, true.toggled())
        assertEquals(true, false.toggled())
    }

    @Test
    fun testStringValue() {
        assertEquals("false", false.stringValue)
        assertEquals("true", true.stringValue)
    }

    @Suppress("ForEachParameterNotUsed")
    @Test
    fun testRandom() {

        var hasTrue = false
        var hasFalse = false

        (0..30).forEach {
            if (Bool.random()) {
                hasTrue = true
            } else {
                hasFalse = true
            }
        }

        assert(hasTrue && hasFalse)
    }
}
