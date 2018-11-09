package jp.co.altonotes.android.kibaan.extension

import org.junit.Assert.assertFalse
import org.junit.Test

class BooleanExtTest {

    @Test
    fun testOptionalTrue() {
        val bool: Boolean? = true
        assert(bool.isTrue)
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
    fun testRandom() {

        var hasTrue = false
        var hasFalse = false

        (0..50).forEach {
            if (Bool.random()) {
                hasTrue = true
            } else {
                hasFalse = true
            }
        }

        assert(hasTrue && hasFalse)
    }
}
