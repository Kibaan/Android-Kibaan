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

    var value: Int
        get() {
            return progress + minimumValue
        }
        set(value) {
            progress = (value - minimumValue)
        }

    var minimumValue: Int = 0

    var maximumValue: Int = 1
        get() {
            return max + minimumValue
        }
        set(value) {
            field = value
            this.max = (value - minimumValue)
        }

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
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.UISlider)
            value = array.getInt(R.styleable.UISlider_value, 0)
            minimumValue = array.getInt(R.styleable.UISlider_minimum, 0)
            maximumValue = array.getInt(R.styleable.UISlider_maximum, 1)
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