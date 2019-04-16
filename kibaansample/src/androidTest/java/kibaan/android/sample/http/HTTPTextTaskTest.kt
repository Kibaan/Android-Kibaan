package kibaan.android.sample.http

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import kibaan.android.http.HTTPTask
import kibaan.android.http.HTTPTextTask
import kibaan.android.sample.MainActivity
import org.junit.*
import org.junit.runner.RunWith
import java.nio.charset.Charset

@RunWith(AndroidJUnit4::class)
class HTTPTextTaskTest {

    @get:Rule
    val activityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private var idlingResource: CountingIdlingResource? = null

    @Before
    fun beforeTest() {
        idlingResource = CountingIdlingResource("test")
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun afterTest() {
        idlingResource?.run {
            IdlingRegistry.getInstance().unregister(this)
            idlingResource = null
        }
    }

    @Test
    fun testUtr8URL() {
        idlingResource?.increment()
        val url = "https://shop.nisshin.oilliogroup.com/"

        activityTestRule.runOnUiThread {
            val api = HTTPTextTask(url = url)
            api.execute {
                Assert.assertTrue(true)
            }.onError { _, _ ->
                Assert.assertTrue(false)
                return@onError HTTPTask.ErrorHandlingStatus.finish
            }.onFinish {
                idlingResource?.decrement()
            }
        }
        Espresso.onIdle()
    }

    @Test
    fun testShiftJisURL() {
        idlingResource?.increment()
        val url = "http://products.nisshin-oillio.com/katei/shokuyouyu/index.php"

        activityTestRule.runOnUiThread {
            val api = HTTPTextTask(url = url)
            api.execute {
                Assert.assertTrue(true)
            }.onError { _, _ ->
                Assert.assertTrue(false)
                return@onError HTTPTask.ErrorHandlingStatus.finish
            }.onFinish {
                idlingResource?.decrement()
            }
        }
        Espresso.onIdle()
    }

    @Test
    fun testNotResponseEncodingURL() {
        idlingResource?.increment()
        val url = "http://www.capcom.co.jp/amusement/index.html"

        activityTestRule.runOnUiThread {
            val api = HTTPTextTask(url = url)
            api.execute {
                Assert.assertTrue(true)
            }.onError { _, _ ->
                Assert.assertTrue(false)
                return@onError HTTPTask.ErrorHandlingStatus.finish
            }.onFinish {
                idlingResource?.decrement()
            }
        }
        Espresso.onIdle()
    }

    @Test
    fun testParseError() {
        idlingResource?.increment()
        val url = "http://www.capcom.co.jp/amusement/index.html"

        activityTestRule.runOnUiThread {
            val api = HTTPTextTask(url = url, defaultEncoding = Charset.forName("Shift-JIS"))
            api.execute {
                Assert.assertTrue(it.contains("�"))
            }.onError { _, _ ->
                Assert.assertTrue(false)
                return@onError HTTPTask.ErrorHandlingStatus.finish
            }.onFinish {
                idlingResource?.decrement()
            }
        }
        Espresso.onIdle()
    }
}