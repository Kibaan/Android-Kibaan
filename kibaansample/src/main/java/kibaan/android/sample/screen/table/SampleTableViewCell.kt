package kibaan.android.sample.screen.table

import android.content.Context
import android.util.AttributeSet
import kibaan.android.ios.UITableViewCell

class SampleTableViewCell : UITableViewCell {

    // region -> Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // endregion

    // region -> Initializer

    init {
        accessoryType = AccessoryType.disclosureIndicator
    }

    // endregion
}