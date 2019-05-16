package kibaan.android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kibaan.android.ios.*
import kotlin.reflect.KClass

open class SmartTableView : UITableView {

    // region -> Variables

    /** PullToRefresh用のレイアウト（外から設定される前提） */
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    /** データなしラベル */
    var noDataLabel: SmartLabel = SmartLabel(context)

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

    // region -> Initializer

    init {
        addView(noDataLabel, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        noDataLabel.isBold = true
        noDataLabel.textColor = UIColor.white
        noDataLabel.backgroundColor = UIColor.clear
        noDataLabel.gravity = Gravity.CENTER
        noDataLabel.isGone = true
    }

    // endregion

    fun <T : UITableViewCell> registerCellClass(type: KClass<T>, isTargetIndex: TargetCheck? = null) {
        val resourceId = resourceId(type)
        register(resourceId, type, isTargetIndex)
    }

    // region -> Support

    /**
     * データ無しラベルの表示 / 非表示を切り替える
     * @param show: 表示するかどうか
     */
    open fun showNoDataLabel(show: Boolean) {
        noDataLabel.isGone = !show
    }

    /**
     * データ無しラベルに表示するテキストを設定する
     * @param noDataMessage: テキスト
     */
    open fun setNoDataMessage(noDataMessage: String?) {
        noDataLabel.text = noDataMessage
    }

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
