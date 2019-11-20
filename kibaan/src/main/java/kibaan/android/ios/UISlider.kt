package kibaan.android.ios

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import kibaan.android.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

interface UISliderDelegate {
    fun onSliderValueChanged(slider: UISlider)
}

open class UISlider : AppCompatSeekBar {

    /**
     * スライダーの分解能。この値で分割した刻みでスライダーの値を取得できる
     */
    private val resolution: Int = Int.MAX_VALUE

    /**
     * SeekBarに設定する最大値。SeekBarのvalueは0始まりのため1ひく
     */
    private val innerMax: Int
        get() = resolution - 1

    var value: Float
        get() {
            return progressToValue(innerProgress).toFloat()
        }
        set(value) {
            var limitedValue = min(value, maximumValue)
            limitedValue = max(limitedValue, minimumValue)

            // プログラムから値を変更した場合は、リスナーを呼びたくないので一度リスナーを外す
            setOnSeekBarChangeListener(null)
            innerProgress = valueToProgress(limitedValue.toDouble())
            progress = innerProgress.roundToInt()
            setOnSeekBarChangeListener(seekBarChangeListener)
        }

    private var innerProgress: Double = 0.0

    var minimumValue: Float = 0.0f
    var maximumValue: Float = 1.0f

    private val rangeWidth: Double get() = (maximumValue - minimumValue).toDouble()

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

    private fun setupUISlider(context: Context, attrs: AttributeSet? = null) {
        max = innerMax
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.UISlider)
            value = array.getFloat(R.styleable.UISlider_value, 0.0f)
            minimumValue = array.getFloat(R.styleable.UISlider_minimum, 0.0f)
            maximumValue = array.getFloat(R.styleable.UISlider_maximum, 1.0f)
            array.recycle()
        }
        setOnSeekBarChangeListener(seekBarChangeListener)
    }

    private fun progressToValue(progress: Double): Double {
        val add = rangeWidth * (progress / resolution.toFloat())
        return minimumValue + add
    }

    private fun valueToProgress(value: Double): Double {
        val rate = (value - minimumValue) / rangeWidth
        return rate * innerMax
    }

    private val seekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            innerProgress = progress.toDouble()
            delegate?.onSliderValueChanged(seekBar as UISlider)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }
}