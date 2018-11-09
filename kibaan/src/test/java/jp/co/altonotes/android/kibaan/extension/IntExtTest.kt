package jp.co.altonotes.android.kibaan.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class IntExtTest {

    @Test
    fun testRandom() {
        val min = -3
        val max = 3

        // test range
        (1..10).forEach {
            val random = Int.random(min = min, max = max)
            assert(random in min..max)
        }
        var random = Int.random(min = 0, max = 0)
        assertEquals(0, random)
        random = Int.random(min = 1, max = 1)
        assertEquals(1, random)
    }

    @Test
    fun testRandomFill() {
        var hasZero = false
        var hasOne = false
        (1..30).forEach {
            val random = Int.random(min = 0, max = 1)
            if (random == 0) {
                hasZero = true
            } else if (random == 1) {
                hasOne = true
            }
        }
        assert(hasZero && hasOne)
    }
}
