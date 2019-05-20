package kibaan.android.ios

import android.content.Context
import android.util.TypedValue
import kibaan.android.ui.RoundedFrameLayout
import kibaan.android.ui.SmartLabel
import kibaan.android.util.DeviceUtils

class UITableViewHeaderFooterView(context: Context) : RoundedFrameLayout(context) {

    var textLabel: SmartLabel? = SmartLabel(context)

    init {
        backgroundColor = UIColor(rgbValue = 0xF5F5F5)
        addView(textLabel)
        textLabel?.setPadding(DeviceUtils.toPx(context, 16), DeviceUtils.toPx(context, 4), DeviceUtils.toPx(context, 10), DeviceUtils.toPx(context, 4))
        textLabel?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        textLabel?.textColor = UIColor(rgbValue = 0x333333)
        textLabel?.isBold = true
    }

    constructor(context: Context, title: String) : this(context) {
        textLabel?.text = title
    }
}
