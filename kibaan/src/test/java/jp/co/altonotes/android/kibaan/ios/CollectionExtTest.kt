package jp.co.altonotes.android.kibaan.ios

import org.junit.Assert.*
import org.junit.Test

class CollectionExtTest {

    @Test
    fun testPlusAssign() {
        val list = mutableListOf("A", "B")
        list += "C"


        assertArrayEquals(arrayOf("A", "B", "C"), list.toTypedArray())
    }

    @Test
    fun testAppendAndRemove() {
        val list = mutableListOf("A")

        assertEquals(list.count, 1)

        list.append("B")
        assertArrayEquals(arrayOf("A", "B"), list.toTypedArray())
        assertEquals(list.count, 2)

        list.append(contentsOf = listOf("C", "D"))
        assertArrayEquals(arrayOf("A", "B", "C", "D"), list.toTypedArray())
        assertEquals(list.count, 4)

        list.remove(listOf("A", "C"))
        assertArrayEquals(arrayOf("B", "D"), list.toTypedArray())

        list.removeAll()
        assertEquals(list.count, 0)
    }

    @Test
    fun testJoined() {
        val list = mutableListOf("A", "B", "C")
        assertEquals("ABC", list.joined())
        assertEquals("A,B,C", list.joined(","))
    }

    @Test
    fun testIndex() {
        val list = mutableListOf("A", "B", "C")

        assertEquals(1, list.index("B"))
        assertEquals(null, list.indexOrNull { it == "D" })
    }

    @Test
    fun testContains() {
        val list = mutableListOf("A", "B", "C")
        assertTrue(list.contains("B"))
        assertFalse(list.contains("D"))
    }

    @Test
    fun testFlatMap() {
        val list = mutableListOf("A", null, "B")
        assertArrayEquals(arrayOf("A", "B"), list.flatMap { it }.toTypedArray())
    }

    @Test
    fun enumerated() {
        val list = mutableListOf("A", "B", "C")

        var count = 0
        list.enumerated().forEach {
            assertEquals(count, it.offset)
            assertEquals(list[it.offset], it.element)
            count++
        }

        assertEquals(3, count)
    }

}
