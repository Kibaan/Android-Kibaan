package jp.co.altonotes.android.kibaan.ios

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ListExtTest {

    @Test
    fun testRemove() {
        val list = mutableListOf("A", "B", "C", "D")

        list.removeFirst()
        assertArrayEquals(arrayOf("B", "C", "D"), list.toTypedArray())

        list.removeLast()
        assertArrayEquals(arrayOf("B", "C"), list.toTypedArray())
    }

    @Test
    fun testRemoveSubRange() {
        var list = mutableListOf("A", "B", "C", "D")
        list.removeSubrange(0..2)
        assertArrayEquals(arrayOf("C", "D"), list.toTypedArray())

        list = mutableListOf("A", "B", "C", "D")
        list.removeSubrange(1..3)
        assertArrayEquals(arrayOf("A", "D"), list.toTypedArray())

    }

    @Test
    fun testSafeGet() {
        val list = listOf("A", "B")

        assertEquals(null, list.safeGet(-1))
        assertEquals("A", list.safeGet(0))
        assertEquals("B", list.safeGet(1))
        assertEquals(null, list.safeGet(2))
    }

    @Test
    fun testInsert() {
        val baseList = listOf("A", "B", "C")

        var list = baseList.toMutableList()
        list.insert("Z", 0)
        assertArrayEquals(arrayOf("Z", "A", "B", "C"), list.toTypedArray())

        list = baseList.toMutableList()
        list.insert("Z", 1)
        assertArrayEquals(arrayOf("A", "Z", "B", "C"), list.toTypedArray())

        list = baseList.toMutableList()
        list.insert("Z", 3)
        assertArrayEquals(arrayOf("A", "B", "C", "Z"), list.toTypedArray())

    }

    @Test
    fun testPrefix() {
        val baseList = listOf("A", "B", "C")

        var list = baseList.prefix(0)
        assertEquals(0, list.count)

        list = baseList.prefix(1)
        assertEquals(1, list.count)
        assertEquals("A", list[0])

        list = baseList.prefix(3)
        assertEquals(3, list.count)
        assertEquals("A", list[0])
        assertEquals("C", list[2])

        list = baseList.prefix(10)
        assertEquals(3, list.count)
    }

    @Test
    fun testSuffix() {
        val baseList = listOf("A", "B", "C")

        var list = baseList.suffix(0)
        assertEquals(0, list.count)

        list = baseList.suffix(1)
        assertEquals(1, list.count)
        assertEquals("C", list[0])

        list = baseList.suffix(3)
        assertEquals(3, list.count)
        assertEquals("A", list[0])
        assertEquals("C", list[2])

        list = baseList.suffix(10)
        assertEquals(3, list.count)
    }

}
