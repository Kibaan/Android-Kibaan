package kibaan.android.ios

import kibaan.android.extension.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CGRectTest {

    @Test
    fun testRect() {
        val rect = CGRect(x = 10, y = 20, width = 100, height = 200)

        assertEquals(10.0, rect.minX, 0.0)
        assertEquals(20.0, rect.minY, 0.0)
        assertEquals(110.0, rect.maxX, 0.0)
        assertEquals(220.0, rect.maxY, 0.0)
    }

    @Test
    fun testExtension() {
        val rect = CGRect(x = 10, y = 20, width = 100, height = 200)

        assertEquals(100.0, rect.shortLength, 0.0)
        assertEquals(200.0, rect.longLength, 0.0)
        assertEquals(100.0, rect.width, 0.0)
        assertEquals(200.0, rect.height, 0.0)
        assertEquals(10.0, rect.x, 0.0)
        assertEquals(20.0, rect.y, 0.0)
    }

}
