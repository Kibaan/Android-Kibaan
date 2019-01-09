package kibaan.android.ios

import android.support.test.runner.AndroidJUnit4
import kibaan.android.extension.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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

    @Test
    fun testZero() {
        val rect = CGRect.zero
        assertEquals(CGRect(0, 0, 0, 0), rect)
    }

    @Test
    fun testRectF() {
        val rect = CGRect(10, 10, 20, 20)
        val rectF = rect.rectF
        assertEquals(10.0f, rectF.left)
        assertEquals(10.0f, rectF.top)
        assertEquals(30.0f, rectF.right)
        assertEquals(30.0f, rectF.bottom)
    }

    @Test
    fun testFloatConstructor() {
        val rect = CGRect(10.0, 20.0, 30.0, 40.0)
        assertEquals(CGRect(origin = CGPoint(10.0, 20.0), size = CGSize(30.0, 40.0)), rect)
    }

    @Test
    fun testIntConstructor() {
        val rect = CGRect(10, 10, 10, 10)
        assertEquals(CGRect(origin = CGPoint(10.0, 10.0), size = CGSize(10.0, 10.0)), rect)
    }

    @Test
    fun testCopy() {
        val rect = CGRect(10, 20, 30, 40)
        val rect2 = CGRect(10, 20, 30, 40)
        assertFalse(rect === rect2)
        assertFalse(rect === rect.copy())
        assertTrue(rect == rect.copy())
    }
}
