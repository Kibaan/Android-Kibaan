package kibaan.android.ios

import org.junit.Assert
import org.junit.Test

class DispatchGroupTest {

    @Test
    fun testNotify1() {
        val group = DispatchGroup()
        var fired = false
        group.notify(queue = DispatchQueue.main) {
            fired = true
        }

        group.enter()
        Assert.assertFalse(fired)
        group.enter()
        Assert.assertFalse(fired)
        group.enter()
        Assert.assertFalse(fired)
        group.leave()
        Assert.assertFalse(fired)
        group.leave()
        Assert.assertFalse(fired)
        group.leave()
        Assert.assertTrue(fired)
    }
}