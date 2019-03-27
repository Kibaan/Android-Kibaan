package kibaan.android.ui

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import kibaan.android.ios.TargetCheck
import kibaan.android.ios.UITableView
import kibaan.android.ios.UITableViewCell
import kotlin.reflect.KClass

open class SmartTableView : UITableView {

    // region -> Variables

    /** PullToRefresh用のレイアウト（外から設定される前提） */
    var swipeRefreshLayout: SwipeRefreshLayout? = null

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

    // region -> Support

    /**
     * スクロール位置を初期化する
     *
     * @param animated アニメーションの有無
     */
    fun resetScrollOffset(animated: Boolean = false) {
        if (animated) {
            recyclerView.smoothScrollToPosition(0)
        } else {
            recyclerView.scrollToPosition(0)
        }
    }

    // endregion

    // region -> Refresh Control

    fun addRefreshControl(swipeRefreshLayout: SwipeRefreshLayout, onPullToRefresh: (() -> Unit)? = null) {
        this.swipeRefreshLayout = swipeRefreshLayout
        swipeRefreshLayout.isEnabled = onPullToRefresh != null
        swipeRefreshLayout.setOnRefreshListener {
            onPullToRefresh?.invoke()
        }
    }

    fun endRefreshing() {
        swipeRefreshLayout?.isRefreshing = false
    }

    // endregion
}
