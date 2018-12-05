package kibaan.android.ui

import android.content.Context
import android.util.AttributeSet
import kibaan.android.ios.TargetCheck
import kibaan.android.ios.UITableView
import kibaan.android.ios.UITableViewCell
import kotlin.reflect.KClass

open class SmartTableView : UITableView {

    // region -> Variables

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context) {
//        setup(context = context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        setup(context = context, attrs = attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        setup(context = context, attrs = attrs)
    }

    // endregion

    fun <T : UITableViewCell> registerCellClass(type: KClass<T>, isTargetIndex: TargetCheck? = null) {
        val resourceId = resourceId(type)
        register(resourceId, type, isTargetIndex)
    }
}
