package kibaan.android.sample.screen.sub

import android.view.View
import kibaan.android.framework.BaseViewController
import kibaan.android.ios.IBAction
import kibaan.android.ios.IBOutlet
import kibaan.android.ios.UIControlState
import kibaan.android.sample.R
import kibaan.android.framework.ScreenService
import kibaan.android.ui.SmartButton

/**
 * ボタン確認用画面
 */
class ButtonViewController : BaseViewController() {

    // region -> Outlets

    @IBOutlet(R.id.set_image_button)
    lateinit var setImageButton: SmartButton

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
//    fun action

    // endregion
}