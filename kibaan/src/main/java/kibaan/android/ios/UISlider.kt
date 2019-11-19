package kibaan.android.ios

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import kibaan.android.R

interface UISliderDelegate {
    fun onSliderValueChanged(slider: UISlider)
}

open class UISlider : AppCompatSeekBar {

    /**
     * スライダーの分解能。この値で分割した刻みでスライダーの値を取得できる
     */
    var resolution: Int = 1000

    var value: Float
        get() {
            return progressToValue(progress)
        }
        set(value) {
            // プログラムから値を変更した場合は、リスナーを呼びたくないので一度リスナーを外す
            setOnSeekBarChangeListener(null)
            val rate = (value - minimumValue) / rangeWidth
            progress = (rate * resolution).toInt()
            setOnSeekBarChangeListener(seekBarChangeListener)
        }

    var minimumValue: Float = 0.0f

    var maximumValue: Float = 1.0f
    val rangeWidth: Float get() = (maximumValue - minimumValue)

    var delegate: UISliderDelegate? = null

    constructor(context: Context) : super(context) {
        setupUISlider(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupUISlider(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupUISlider(context, attrs)
    }

    private fun progressToValue(progress: Int): Float {
        val add = rangeWidth * (progress / resolution.toFloat())
        return minimumValue + add
    }

    private fun setupUISlider(context: Context, attrs: AttributeSet? = null) {
        max = resolution
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.UISlider)
            value = array.getFloat(R.styleable.UISlider_value, 0.0f)
            minimumValue = array.getFloat(R.styleable.UISlider_minimum, 0.0f)
            maximumValue = array.getFloat(R.styleable.UISlider_maximum, 1.0f)
            array.recycle()
        }
        setOnSeekBarChangeListener(seekBarChangeListener)
    }

    private val seekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
            delegate?.onSliderValueChanged(p0 as UISlider)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {}
        override fun onStopTrackingTouch(p0: SeekBar) {}
    }
}