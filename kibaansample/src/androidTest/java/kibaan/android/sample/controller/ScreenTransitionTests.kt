package kibaan.android.sample.controller

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import kibaan.android.framework.ScreenService
import kibaan.android.framework.ViewControllerCache
import kibaan.android.sample.MainActivity
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenTransitionTests {

    @get:Rule
    val activityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testViewControllerInit() {
        activityTestRule.runOnUiThread {
            ViewControllerCache.shared.clear()
            val vc = MockViewController()
            Assert.assertEquals(0, vc.startCount)
            Assert.assertEquals(0, vc.stopCount)
        }
    }

    @Test
    fun testSetRoot() {
        activityTestRule.runOnUiThread {
            ViewControllerCache.shared.clear()
            val vc1 = ScreenService.shared.setRoot(MockViewController::class)

            // 1.rootViewControllerのonStartが呼ばれ、isForegroundがtrueであること
            Assert.assertEquals(1, vc1.startCount)
            Assert.assertTrue(vc1.isForeground)

            // 2.foregroundSubControllersのonStartも呼ばれ、isForegroundがtrueであること
            Assert.assertEquals(1, vc1.subVc1.startCount)
            Assert.assertTrue(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.startCount)
            Assert.assertFalse(vc1.subVc2.isForeground)

            ScreenService.shared.setRoot(MockSubViewController::class)

            // 3.rootViewControllerのonStopが呼ばれ、isForegroundがfalseであること
            Assert.assertEquals(1, vc1.stopCount)
            Assert.assertFalse(vc1.isForeground)

            // 4.foregroundSubControllersのonStopも呼ばれ、isForegroundがfalseであること
            Assert.assertEquals(1, vc1.subVc1.stopCount)
            Assert.assertFalse(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)
        }
    }

    @Test
    fun testAddSubscreen() {
        activityTestRule.runOnUiThread {
            ViewControllerCache.shared.clear()
            val vc1 = ScreenService.shared.setRoot(MockViewController::class)
            val addVc = ScreenService.shared.addSubScreen(MockViewController::class, id = "2")!!

            // 1.Addしたスクリーンの確認
            Assert.assertEquals(1, addVc.startCount)
            Assert.assertTrue(addVc.isForeground)
            Assert.assertEquals(1, addVc.subVc1.startCount)
            Assert.assertTrue(addVc.subVc1.isForeground)
            Assert.assertEquals(0, addVc.subVc2.startCount)
            Assert.assertFalse(addVc.subVc2.isForeground)

            // 2.Rootスクリーンの確認
            Assert.assertEquals(1, vc1.stopCount)
            Assert.assertFalse(vc1.isForeground)
            Assert.assertEquals(1, vc1.subVc1.stopCount)
            Assert.assertFalse(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)

            ScreenService.shared.removeSubScreen()

            // 3.Removeしたスクリーンの確認
            Assert.assertEquals(1, addVc.stopCount)
            Assert.assertFalse(addVc.isForeground)
            Assert.assertEquals(1, addVc.subVc1.stopCount)
            Assert.assertFalse(addVc.subVc1.isForeground)
            Assert.assertEquals(0, addVc.subVc2.stopCount)
            Assert.assertFalse(addVc.subVc2.isForeground)

            // 4.Rootスクリーンの確認
            Assert.assertEquals(2, vc1.startCount)
            Assert.assertTrue(vc1.isForeground)
            Assert.assertEquals(2, vc1.subVc1.startCount)
            Assert.assertTrue(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)
        }
    }

    @Test
    fun testAddOverlay() {
        activityTestRule.runOnUiThread {
            ViewControllerCache.shared.clear()
            val vc1 = ScreenService.shared.setRoot(MockViewController::class)
            val addVc = vc1.addOverlay(MockSubViewController::class)!!

            // 1.Addしたスクリーンの確認
            Assert.assertEquals(1, addVc.startCount)
            Assert.assertTrue(addVc.isForeground)

            // 2.Rootスクリーンの確認
            Assert.assertEquals(0, vc1.stopCount)
            Assert.assertTrue(vc1.isForeground)
            Assert.assertEquals(0, vc1.subVc1.stopCount)
            Assert.assertTrue(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)

            vc1.removeOverlay()

            // 3.Removeしたスクリーンの確認
            Assert.assertEquals(1, addVc.stopCount)
            Assert.assertFalse(addVc.isForeground)

            // 4.Rootスクリーンの確認
            Assert.assertEquals(1, vc1.startCount)
            Assert.assertTrue(vc1.isForeground)
            Assert.assertEquals(1, vc1.subVc1.startCount)
            Assert.assertTrue(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)
        }
    }


    @Test
    fun testRemoveAllOverlay() {
        activityTestRule.runOnUiThread {
            ViewControllerCache.shared.clear()
            val vc1 = ScreenService.shared.setRoot(MockViewController::class)
            val addVc1 = vc1.addOverlay(MockSubViewController::class, cache = false)!!
            val addVc2 = vc1.addOverlay(MockSubViewController::class, cache = false)!!

            // 1.Addしたスクリーンの確認
            Assert.assertEquals(1, addVc1.startCount)
            Assert.assertTrue(addVc1.isForeground)
            Assert.assertEquals(1, addVc2.startCount)
            Assert.assertTrue(addVc2.isForeground)

            // 2.Rootスクリーンの確認
            Assert.assertEquals(0, vc1.stopCount)
            Assert.assertTrue(vc1.isForeground)
            Assert.assertEquals(0, vc1.subVc1.stopCount)
            Assert.assertTrue(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)

            vc1.removeAllOverlay()

            // 3.Removeしたスクリーンの確認
            Assert.assertEquals(1, addVc1.stopCount)
            Assert.assertFalse(addVc1.isForeground)
            Assert.assertEquals(1, addVc2.stopCount)
            Assert.assertFalse(addVc2.isForeground)

            // 4.Rootスクリーンの確認
            Assert.assertEquals(1, vc1.startCount)
            Assert.assertTrue(vc1.isForeground)
            Assert.assertEquals(1, vc1.subVc1.startCount)
            Assert.assertTrue(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)

            // 5.オーバーレイが全て消えていること
            Assert.assertFalse(vc1.hasOverlay)
        }
    }

    fun testAddNextScreen() {
        activityTestRule.runOnUiThread {
            ViewControllerCache.shared.clear()
            val vc1 = ScreenService.shared.setRoot(MockViewController::class)
            val addVc = vc1.addNextScreen(MockSubViewController::class)!!

            // 1.Addしたスクリーンの確認
            Assert.assertEquals(1, addVc.startCount)
            Assert.assertTrue(addVc.isForeground)

            // 2.Rootスクリーンの確認
            Assert.assertEquals(1, vc1.startCount)
            Assert.assertFalse(vc1.isForeground)
            Assert.assertEquals(1, vc1.subVc1.stopCount)
            Assert.assertFalse(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)

            vc1.removeNextScreen()

            // 3.Removeしたスクリーンの確認
            Assert.assertEquals(1, addVc.stopCount)
            Assert.assertFalse(addVc.isForeground)

            // 4.Rootスクリーンの確認
            Assert.assertEquals(2, vc1.startCount)
            Assert.assertTrue(vc1.isForeground)
            Assert.assertEquals(2, vc1.subVc1.startCount)
            Assert.assertTrue(vc1.subVc1.isForeground)
            Assert.assertEquals(0, vc1.subVc2.stopCount)
            Assert.assertFalse(vc1.subVc2.isForeground)
        }
    }

}