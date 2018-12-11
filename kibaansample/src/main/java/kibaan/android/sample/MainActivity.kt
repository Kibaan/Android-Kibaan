package kibaan.android.sample

import android.os.Bundle
import kibaan.android.framework.ScreenService
import kibaan.android.framework.SmartActivity
import kibaan.android.sample.screen.top.TopViewController

class MainActivity : SmartActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenService.shared.setRoot(TopViewController::class)
    }
}
