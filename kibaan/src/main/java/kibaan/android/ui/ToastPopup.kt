package kibaan.android.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import kibaan.android.AndroidUnique
import kibaan.android.framework.SmartActivity
import kibaan.android.ios.IntEnumDefault
import kibaan.android.ios.UIColor
import kibaan.android.ios.append
import kibaan.android.ios.removeFromSuperview
import kibaan.android.util.DeviceUtils
import kotlin.math.min

class ToastPopup : AppCompatTextView {

    // region -> Constants

    enum class DisplayType : IntEnumDefault {
        NORMAL, ERROR;

        fun backgroundColor(context: Context): UIColor {
            return when (this) {
                NORMAL -> UIColor.white
                ERROR -> UIColor.red
            }
        }

        val textColor: UIColor
            get() {
                return when (this) {
                    NORMAL -> UIColor(Color.BLACK)
                    ERROR -> UIColor(Color.WHITE)
                }
            }
    }

    // endregion

    // region -> Constructor
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    // endregion

    // region -> Variables

    private val animationDuration: Long = 400
    private var displayTime: Long = 3000
    private var slideHandler: Handler = Handler()
    private val slideOutRunnable: Runnable = Runnable {
        kotlin.run {
            slideOut()
        }
    }
    private var previousTouchY: Float = 0.0f
    @AndroidUnique
    private var slideOutAnimator: ViewPropertyAnimator? = null

    // endregion

    // region -> Static

    companion object {
        private var padding: Int = DeviceUtils.vminLength(SmartActivity.shared, 0.025).toInt()
        private var cornerRadius: Float = DeviceUtils.vminLength(SmartActivity.shared, 0.01).toFloat()
        private var topY: Int = DeviceUtils.vminLength(SmartActivity.shared, 0.01).toInt()
        private var queue: MutableList<ToastPopup> = mutableListOf()

        fun show(message: String, type: DisplayType = DisplayType.NORMAL, displayTime: Long = 3000) {
            hideAllToastPopup()
            val toast = create(message, type)
            queue.append(toast)
            toast.displayTime = displayTime
            SmartActivity.shared.rootContainer.addView(toast)
            toast.adjustLayout()
            toast.scheduledTimer(0, Runnable { kotlin.run { toast.start() } })
        }

        private fun create(message: String, type: DisplayType): ToastPopup {
            val width = DeviceUtils.vminLength(SmartActivity.shared, 0.96).toInt()
            val toast = ToastPopup(SmartActivity.shared)
            toast.layoutParams = ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            toast.setBackground(type)
            toast.setTextColor(type.textColor.intValue)
            toast.text = message
            toast.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
            toast.setPadding(padding, padding, padding, padding)
            toast.visibility = View.INVISIBLE
            return toast
        }

        private fun hideAllToastPopup() {
            queue.forEach { it.forceHide() }
        }
    }
    // endregion

    private fun setBackground(type: DisplayType) {
        background = gradientDrawable(type)
    }

    private fun gradientDrawable(with: DisplayType): GradientDrawable {
        val intArray = intArrayOf(with.backgroundColor(context).intValue, with.backgroundColor(context).intValue)
        val drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArray)
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = cornerRadius
        return drawable
    }

    private fun start() {
        slideIn()
        scheduledTimer(displayTime, slideOutRunnable)
    }

    private fun slideIn(fromStart: Boolean = true) {
        if (fromStart) {
            val outerPosition = -(top + height.toFloat())
            animate().setDuration(0).translationY(outerPosition).start()
        }
        val toPosition = if (fromStart) topY else topY - top
        animate().setDuration(animationDuration).translationY(toPosition.toFloat()).withStartAction {
            visibility = View.VISIBLE
        }.start()
    }

    private fun slideOut(delay: Long = 0) {
        val toPosition = -(top + height.toFloat())
        slideOutAnimator = animate().setStartDelay(delay).setDuration(animationDuration).translationY(toPosition).withEndAction {
            SmartActivity.shared.runOnUiThread {
                removeFromSuperview()
            }
        }
    }

    // region -> Timer

    private fun scheduledTimer(displayTime: Long, runnable: Runnable) {
        slideHandler.postDelayed(runnable, displayTime)
    }

    private fun stopTimer() {
        slideHandler.removeCallbacks(slideOutRunnable)
    }
    // endregion

    // region -> Touch Event

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val newDy = event?.rawY ?: return super.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_DOWN) {
            stopTimer()
            previousTouchY = newDy
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            val move = newDy - previousTouchY
            val top = min(0, (top + move).toInt())
            val bottom = top + height
            layout(left, top, right, bottom)
            previousTouchY = newDy
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            if (top < (height * -0.2)) {
                slideOut()
            } else {
                slideIn(fromStart = false)
                scheduledTimer(displayTime, slideOutRunnable)
            }
        }
        return true
    }

    // endregion

    // region -> Other

    private fun adjustLayout() {
        (layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.CENTER_HORIZONTAL
    }

    private fun forceHide() {
        stopTimer()
        this.removeFromSuperview()
    }


    // endregion

}
