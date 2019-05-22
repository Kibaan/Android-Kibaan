package kibaan.android.ios

import org.junit.Assert.*
import org.junit.Test

class IterableExtTest {

    @Test
    fun reduce() {
        val intList = listOf(2, 5, 1)
        assertEquals(8, intList.reduce(0) { acc, i ->
            acc + i
        })
        assertEquals(8, intList.reduce(0, Int::plus))

        val stringList = listOf("あ", "い", "う")
        assertEquals("あいう", stringList.reduce("", String::plus))
        assertEquals("251", intList.reduce("", String::plus))

        val listList = listOf(listOf("あ", "い", "う"), listOf("え", "お"))
        assertArrayEquals(arrayOf("あ", "い", "う", "え", "お"), listList.reduce(listOf<String>(), { acc, s ->
            acc + s
        }).toTypedArray())
    }

    @Test
    fun first() {
        val intList = listOf(2, 5, 1)
        assertEquals(2, intList.first)
    }

    @Test
    fun last() {
        val intList = listOf(2, 5, 1)
        assertEquals(1, intList.last)
    }

    @Test
    fun min() {
        val intList = listOf(2, 5, 1)
        assertEquals(1, intList.min { o1, o2 -> o1 < o2 })
    }

    @Test
    fun max() {
        val intList = listOf(2, 5, 1)
        assertEquals(5, intList.max { o1, o2 -> o1 > o2 })
    }
}
