package kibaan.android.sample

import android.os.Bundle
import kibaan.android.service.ScreenService
import kibaan.android.controller.SmartActivity
import kibaan.android.sample.screen.top.TopViewController

class MainActivity : SmartActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenService.shared.setRoot(TopViewController::class)
    }
}
