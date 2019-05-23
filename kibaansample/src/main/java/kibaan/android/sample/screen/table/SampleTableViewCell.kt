package kibaan.android.sample.screen.table

import android.content.Context
import android.util.AttributeSet
import kibaan.android.extension.stringValue
import kibaan.android.ios.IBOutlet
import kibaan.android.ios.UITableViewCell
import kibaan.android.ios.didSet
import kibaan.android.sample.R
import kibaan.android.ui.SmartLabel

class SampleTableViewCell : UITableViewCell {

    // region -> Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // endregion

    // region -> Outlets

    @IBOutlet(R.id.indexLabel) lateinit var indexLabel: SmartLabel

    // endregion

    // region -> Initializer

    init {
        accessoryType = AccessoryType.disclosureIndicator
    }

    // endregion

    var index: Int by didSet(0) {
        indexLabel.text = index.stringValue
    }
}