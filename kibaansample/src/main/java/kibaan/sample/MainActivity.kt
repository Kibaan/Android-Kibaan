package kibaan.sample

import android.os.Bundle
import kibaan.service.ScreenService
import kibaan.controller.SmartActivity
import kibaan.sample.screen.top.TopViewController

class MainActivity : SmartActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenService.shared.setRoot(TopViewController::class)
    }
}
