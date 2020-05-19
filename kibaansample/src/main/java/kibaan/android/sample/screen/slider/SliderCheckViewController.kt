package kibaan.android.sample.screen.slider

import android.view.View
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.IBAction
import kibaan.android.ios.IBOutlet
import kibaan.android.ios.UISlider
import kibaan.android.ios.UISliderDelegate
import kibaan.android.sample.R
import kibaan.android.ui.SmartLabel

/**
 * サブ画面
 */
class SliderCheckViewController : SmartViewController(), UISliderDelegate {

    @IBOutlet(R.id.slider) lateinit var slider: UISlider
    @IBOutlet(R.id.valueLabel) lateinit var valueLabel: SmartLabel

    override fun viewDidLoad() {
        super.viewDidLoad()

        slider.minimumValue = 1.0f
        slider.maximumValue = 255.0f
        slider.delegate = this
    }

    @IBAction(R.id.plusButton)
    fun plusAction(sender: View) {
        slider.value = slider.value + 1
        valueLabel.text = slider.value.toString()
    }

    @IBAction(R.id.minusButton)
    fun minusAction(sender: View) {
        slider.value = slider.value - 1
        valueLabel.text = slider.value.toString()
    }

    override fun onSliderValueChanged(slider: UISlider) {
        valueLabel.text = slider.value.toString()
    }
}