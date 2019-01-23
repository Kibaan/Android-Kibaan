package kibaan.android.framework

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.FrameLayout
import kibaan.android.ios.UIViewController
import kibaan.android.ui.SmartContext

/**
 * SMART基盤を使用する場合、MainのActivityに継承させる
 */
@SuppressLint("Registered")
open class SmartActivity : AppCompatActivity() {

    lateinit var rootContainer: RootFrameLayout
    var isUserInteractionEnabled = true

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
        rootContainer = RootFrameLayout(this)
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
        SingletonContainer.clear()
    }

    // endregion

    // region -> Action

    override fun onBackPressed() {
        if (!isUserInteractionEnabled) {
            return
        }
        if (!ScreenService.shared.goBack()) {
            finishProcess()
        }
    }

    open fun finishProcess() {
        finish()
    }

    // endregion

    inner class RootFrameLayout(context: Context) : FrameLayout(context) {

        override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
            if (!isUserInteractionEnabled) {
                return true
            }
            return super.onInterceptTouchEvent(ev)
        }
    }
}