package kibaan.android.sample.model

import kibaan.android.framework.SingletonContainer
import kibaan.android.storage.LocalStorage

class AppSetting: LocalStorage() {

    companion object {
        val shared: AppSetting get() = SingletonContainer.shared.get(AppSetting::class)
    }

    var sampleText: String?
        get() {
            return getStringOrNil("sampleText")
        }
        set(value) {
            setStringOrNil("sampleText", value = value)
        }
}