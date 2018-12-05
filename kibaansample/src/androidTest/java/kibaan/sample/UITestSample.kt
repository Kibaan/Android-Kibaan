package kibaan.sample

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import kibaan.sample.screen.top.TopViewController
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
            controller?.testButton?.performClick()
        }

        Thread.sleep(100)

    }

}