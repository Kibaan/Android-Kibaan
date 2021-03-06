package kibaan.android.ios

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
        list.removeSubrange(0..0)
        assertArrayEquals(arrayOf("B", "C", "D"), list.toTypedArray())

        list = mutableListOf("A", "B", "C", "D")
        list.removeSubrange(0..1)
        assertArrayEquals(arrayOf("C", "D"), list.toTypedArray())

        list = mutableListOf("A", "B", "C", "D")
        list.removeSubrange(1..2)
        assertArrayEquals(arrayOf("A", "D"), list.toTypedArray())

    }

    @Test
    fun testRemoveSubRangeDup() {
        var list = mutableListOf("A", "A", "B", "B", "C", "C")
        list.removeSubrange(0..2)
        assertArrayEquals(arrayOf("B", "C", "C"), list.toTypedArray())

        list = mutableListOf("A", "A", "B", "B", "C", "C")
        list.removeSubrange(1..3)
        assertArrayEquals(arrayOf("A", "C", "C"), list.toTypedArray())
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

    @Test
    fun testRangeAccess() {
        val list = listOf(0, 1, 2, 3, 4)

        assertArrayEquals(listOf(0, 1, 2).toIntArray(), list.subList(0..2).toIntArray())
        assertArrayEquals(listOf(0, 1).toIntArray(), list.subList(0 until 2).toIntArray())
        assertArrayEquals(listOf(1, 2, 3).toIntArray(), list.subList(1..3).toIntArray())
        assertArrayEquals(listOf(1, 2).toIntArray(), list.subList(1 until 3).toIntArray())
    }

    @Test
    fun testRangeIndexerAccess() {
        val list = listOf(0, 1, 2, 3, 4)

        assertArrayEquals(listOf(0, 1, 2).toIntArray(), list[0..2].toIntArray())
        assertArrayEquals(listOf(0, 1).toIntArray(), list[0 until 2].toIntArray())
        assertArrayEquals(listOf(1, 2, 3).toIntArray(), list[1..3].toIntArray())
        assertArrayEquals(listOf(1, 2).toIntArray(), list[1 until 3].toIntArray())
    }

}
