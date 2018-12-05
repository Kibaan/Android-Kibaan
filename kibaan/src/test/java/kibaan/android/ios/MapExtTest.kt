package kibaan.android.ios

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapExtTest {

    @Test
    fun testRemoveAll() {
        val map = mutableMapOf(
                "key1" to "value1",
                "key2" to "value2",
                "key3" to "value3"
        )

        map.removeAll()

        assertEquals(0, map.size)
    }

    @Test
    fun testFirst() {
        val map = mutableMapOf(
                "key1" to "value1",
                "key2" to "value2",
                "key3" to "value3",
                "key4" to "value1"
        )

        var result = map.first { it.key == "key2" }
        assertEquals("value2", result?.value)

        result = map.first { it.value == "value3" }
        assertEquals("key3", result?.key)

        result = map.first { it.key == "ないやつ" }
        assertNull(result)

    }
}
