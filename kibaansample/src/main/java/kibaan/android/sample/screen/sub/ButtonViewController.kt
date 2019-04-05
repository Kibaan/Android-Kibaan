package kibaan.android.sample.screen.sub

import android.view.View
import kibaan.android.extension.toggled
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.IBAction
import kibaan.android.ios.IBOutlet
import kibaan.android.ios.UIControlState
import kibaan.android.sample.R
import kibaan.android.framework.ScreenService
import kibaan.android.ios.UIButton
import kibaan.android.ui.SmartButton

/**
 * ボタン確認用画面
 */
class ButtonViewController : SmartViewController() {

    // region -> Outlets

    @IBOutlet(R.id.set_image_button) lateinit var setImageButton: SmartButton
    @IBOutlet(R.id.text_color_button) lateinit var textColorButton :UIButton

    // endregion

    // region -> Variables

    private var count = 0

    // endregion

    // region -> Action

    @IBAction(R.id.close_button)
    fun actionCloseButton(sender: View) {
        ScreenService.shared.removeSubScreen()
    }

    @IBAction(R.id.set_image_change_button)
    fun actionSetImageChangeButton(sender: View) {
        if (count % 2 == 0) {
            setImageButton.setImage(R.mipmap.checkbox_off, state = UIControlState.normal)
        } else {
            setImageButton.setImage(R.mipmap.checkbox_on, state = UIControlState.normal)
        }
        count++
    }

    @IBAction(R.id.text_color_button)
    fun actionTextColorButton(sender: View) {
        textColorButton.isSelected = textColorButton.isSelected.toggled()
    }

//    fun action

    // endregion
}