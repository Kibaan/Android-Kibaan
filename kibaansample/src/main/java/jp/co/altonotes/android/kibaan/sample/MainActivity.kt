package jp.co.altonotes.android.kibaan.sample

import android.os.Bundle
import jp.co.altonotes.android.kibaan.service.ScreenService
import jp.co.altonotes.android.kibaan.controller.SmartActivity
import jp.co.altonotes.android.kibaan.sample.screen.top.TopViewController

class MainActivity : SmartActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenService.shared.setRoot(TopViewController::class)
    }
}
