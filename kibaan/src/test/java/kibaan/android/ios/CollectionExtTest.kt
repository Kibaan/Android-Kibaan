package kibaan.android.ios

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
        val list = mutableListOf("A", "B", "C", "C")

        assertEquals(1, list.index("B"))
        assertEquals(null, list.indexOrNull { it == "D" })

        assertEquals(null, list.firstIndex { it == "D" })
        assertEquals(2, list.firstIndex { it == "C" })
    }

    @Test
    fun testContains() {
        val list = mutableListOf("A", "B", "C")
        assertTrue(list.contains("B"))
        assertFalse(list.contains("D"))
    }
    
    @Test
    fun testCompactMap() {
        val list = mutableListOf("A", null, "B")
        assertArrayEquals(arrayOf("A", "B"), list.compactMap { it }.toTypedArray())
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

    @Test
    fun sorted() {
        val list = listOf(2, 5, 1, 5, 3, 4)

        var sorted = list.sorted { lhs, rhs ->
            when {
                lhs < rhs -> -1
                lhs > rhs -> 1
                else -> 0
            }
        }
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 5), sorted.toTypedArray())

        sorted = list.sorted { lhs, rhs ->
            when {
                lhs <= rhs -> -1
                lhs > rhs -> 1
                else -> 0
            }
        }
        assertArrayEquals(arrayOf(1, 2, 3, 4, 5, 5), sorted.toTypedArray())

        sorted = list.sorted { lhs, rhs ->
            when {
                lhs > rhs -> -1
                lhs < rhs -> 1
                else -> 0
            }
        }
        assertArrayEquals(arrayOf(5, 5, 4, 3, 2, 1), sorted.toTypedArray())

        sorted = list.sorted { lhs, rhs ->
            when {
                lhs >= rhs -> -1
                lhs < rhs -> 1
                else -> 0
            }
        }
    }
}
