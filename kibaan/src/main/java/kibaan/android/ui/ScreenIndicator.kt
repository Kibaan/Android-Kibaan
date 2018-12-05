package kibaan.android.ui

import android.R
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import kibaan.android.ios.UIColor
import kibaan.android.ios.backgroundColor
import kibaan.android.util.DeviceUtils

/**
 * 全画面表示のインジケーター
 * Created by yamamoto on 2018/02/06.
 */
class ScreenIndicator : RelativeLayout {

    private lateinit var progressBar: ProgressBar
    private lateinit var label: SmartLabel

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    /**
     * 構築
     */
    private fun setup() {
        backgroundColor = UIColor(rgbValue = 0x000000, alpha = 0.7)

        // インジケーターを追加
        progressBar = ProgressBar(context, null, R.attr.progressBarStyleLarge)

        val params = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        progressBar.layoutParams = params

        addView(progressBar)

        // ラベルを追加
        label = SmartLabel(context)
        label.textColor = UIColor.white
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.0f)

        val labelParams = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        labelParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        label.setPadding(0, 0, 0, DeviceUtils.toPx(context, 120))
        label.layoutParams = labelParams

        addView(label)
    }

    fun showText(text: String?) {
        label.text = text
        progressBar.visibility = View.VISIBLE
    }

    /**
     * タッチを裏のViewに伝えないために true を返す
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        return true
    }
}