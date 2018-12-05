package kibaan.android.util

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeviceUtilsTest {

    @Test
    fun testLength() {
        val context = InstrumentationRegistry.getTargetContext()

        val shortPx = DeviceUtils.shortLengthPX(context)
        val longPx = DeviceUtils.longLengthPX(context)

        assert(0 < shortPx)
        assert(0 < longPx)
        assert(shortPx <= longPx)

        val shortDp = DeviceUtils.shortLengthDP(context)
        val longDp = DeviceUtils.longLengthDP(context)

        assert(0 < shortDp)
        assert(0 < longDp)
        assert(shortDp <= longDp)
    }

}
