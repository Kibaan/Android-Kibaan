package kibaan.sample.screen.connection

import android.view.View
import kibaan.controller.BaseViewController
import kibaan.extension.integerValue
import kibaan.extension.stringValue
import kibaan.ios.IBAction
import kibaan.ios.IBOutlet
import kibaan.sample.R
import kibaan.sample.api.UsersAPI
import kibaan.service.ScreenService
import kibaan.ui.SmartLabel

/**
 * 通信関連確認用のビューコントローラ
 */
class ConnectionViewController : BaseViewController() {

    // region -> Outlets

    @IBOutlet(R.id.label_1) lateinit var label1: SmartLabel
    @IBOutlet(R.id.label_2) lateinit var label2: SmartLabel
    @IBOutlet(R.id.label_3) lateinit var label3: SmartLabel

    // endregion

    // region -> Variables

    var requestId: String = "1"

    // endregion

    // region -> Life cycle

    override fun onEnterForeground() {
        super.onEnterForeground()
        requestUsersApi(id = requestId)
    }

    override fun onLeaveForeground() {
        super.onLeaveForeground()
    }

    private fun requestUsersApi(id: String) {
        val api = UsersAPI(id = id, owner = taskHolder, key = "Users")
        api.autoRefreshInterval = 1.0
        api.execute { response ->
            label1.text = response.displayId
            label2.text = response.displayName
            label3.text = response.displayEmail
        }
        api.setAutoRefresh {
            var nextId = (id.integerValue + 1)
            if (10 < nextId) {
                nextId = 1
            }
            requestId = nextId.stringValue
            requestUsersApi(requestId)
        }
    }


    // endregion

    // region -> Action

    @IBAction(R.id.close_button)
    fun actionCloseButton(sender: View) {
        ScreenService.shared.removeSubScreen()
    }

    // endregion
}