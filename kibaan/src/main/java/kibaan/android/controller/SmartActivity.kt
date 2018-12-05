package kibaan.android.controller

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import kibaan.android.ios.UIViewController
import kibaan.android.service.ScreenService
import kibaan.android.service.SingletonService
import kibaan.android.ui.SmartContext

/**
 * SMART基盤を使用する場合、MainのActivityに継承させる
 */
@SuppressLint("Registered")
open class SmartActivity : AppCompatActivity() {

    lateinit var rootContainer: FrameLayout

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var sInstance: SmartActivity? = null
        val shared: SmartActivity get() = sInstance!!
        val sharedOrNull: SmartActivity? get() = sInstance
    }

    // region -> Life cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sInstance = this
        rootContainer = FrameLayout(this)
        setContentView(rootContainer, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))

        UIViewController.setActivity(this)
        ScreenService.setActivity(this)
        SmartContext.shared.setActivity(this)
    }

    override fun onResume() {
        super.onResume()
        ScreenService.shared.foregroundController?.enter()
    }

    override fun onPause() {
        super.onPause()
        ScreenService.shared.foregroundController?.leave()
    }

    override fun onDestroy() {
        super.onDestroy()
        UIViewController.setActivity(null)
        ViewControllerCache.clear()
        SingletonService.clear()
    }

    // endregion

    // region -> Action

    override fun onBackPressed() {
        if (!ScreenService.shared.goBack()) {
            finishProcess()
        }
    }

    open fun finishProcess() {
        finish()
    }

    // endregion
}
