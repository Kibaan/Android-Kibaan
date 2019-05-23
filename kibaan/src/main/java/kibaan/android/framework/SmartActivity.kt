package kibaan.android.framework

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import kibaan.android.ios.UIViewController
import kibaan.android.ui.SmartContext
import kibaan.android.util.DeviceUtils

/**
 * SMART基盤を使用する場合、MainのActivityに継承させる
 */
@SuppressLint("Registered")
open class SmartActivity : AppCompatActivity() {

    lateinit var rootContainer: RootFrameLayout
    var isUserInteractionEnabled = true
    // SingletonContainerのインスタンスがActivityが生きているうちに破棄されないよう保持する
    var singletonContainer: SingletonContainer? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var sInstance: SmartActivity? = null
        val shared: SmartActivity get() = sInstance!!
        val sharedOrNull: SmartActivity? get() = sInstance
    }

    // region -> Life cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        singletonContainer = SingletonContainer.shared
        sInstance = this
        rootContainer = RootFrameLayout(this)
        setContentView(
            rootContainer,
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        )

        UIViewController.setGlobalContext(this)
        ScreenService.setActivity(this)
        SmartContext.shared.setActivity(this)

        addKeyboardVisibilityObserver()
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
        UIViewController.setGlobalContext(null)
        ViewControllerCache.shared.clear()
        SingletonContainer.shared.clear()
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

    // region -> Keyboard

    private fun addKeyboardVisibilityObserver() {
        val activityRootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            private var wasVisible: Boolean = false
            private var windowVisibleDisplayFrameRect = Rect()

            override fun onGlobalLayout() {
                activityRootView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrameRect)
                val heightDiff = activityRootView.rootView.height - windowVisibleDisplayFrameRect.height()
                val isVisible = heightDiff > DeviceUtils.toPx(context = activityRootView.context, dp = 100)
                if (isVisible == wasVisible) {
                    return
                }
                wasVisible = isVisible
                (currentFocus as? OnKeyboardVisibilityListener)?.onKeyboardVisibilityChanged(isVisible = isVisible)
            }
        })
    }

    // endregion

    // region -> Inner class

    inner class RootFrameLayout(context: Context) : FrameLayout(context) {

        override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
            if (!isUserInteractionEnabled) {
                return true
            }
            return super.onInterceptTouchEvent(ev)
        }
    }

    // endregion
}

interface OnKeyboardVisibilityListener {
    fun onKeyboardVisibilityChanged(isVisible: Boolean)
}