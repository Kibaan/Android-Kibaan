package kibaan.android.sample

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import kibaan.android.extension.localizedString
import kibaan.android.sample.screen.top.TopViewController
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class UITestSample {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testSample() {
        var controller: TopViewController? = null

        activityTestRule.runOnUiThread {
            controller = TopViewController()
            activityTestRule.activity.rootContainer.addView(controller?.view)
        }

        Thread.sleep(100)

        activityTestRule.runOnUiThread {
//            controller?.testButton?.performClick()
        }

        Thread.sleep(100)

    }

    @Test
    fun testLocalizedString() {
        activityTestRule.runOnUiThread {
            Assert.assertEquals("これはローカライズ文言取得のテストです。", "message_01".localizedString)
            Assert.assertEquals("これはローカライズ文言取得のテストです。", R.string.message_01.localizedString)
        }
    }
}