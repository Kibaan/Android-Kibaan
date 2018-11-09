package jp.co.altonotes.android.kibaan.ui

import android.content.Context
import android.util.AttributeSet
import jp.co.altonotes.android.kibaan.ios.TargetCheck
import jp.co.altonotes.android.kibaan.ios.UITableView
import jp.co.altonotes.android.kibaan.ios.UITableViewCell
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
