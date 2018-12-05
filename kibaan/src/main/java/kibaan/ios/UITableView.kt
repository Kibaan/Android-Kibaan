package kibaan.ios

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kibaan.extension.toSnakeCase
import kibaan.R
import kibaan.ui.TouchLock
import kotlin.reflect.KClass

val UITableViewAutomaticDimension: CGFloat = Double.MIN_VALUE
typealias TargetCheck = ((IndexPath) -> Boolean)

open class UITableView : FrameLayout {

    // region -> IBInspectable

    /** 一覧が選択可能かどうか */
    @IBInspectable var allowsSelection: Boolean = true
    /** 罫線の色 */
    @IBInspectable open var separatorColor: Int = Color.BLACK
        set(value) {
            field = value
            (0 until recyclerView.itemDecorationCount).mapNotNull { recyclerView.getItemDecorationAt(it) as? DividerItemDecoration }.forEach {
                recyclerView.removeItemDecoration(it)
            }
            recyclerView.addItemDecoration(createDividerItemDecoration())
        }
    /** 編集可能かどうか */
    var isEditing: Boolean = false
        set(value) {
            field = value
            adapter?.notifyDataSetChanged()
        }

    // endregion

    // region -> Variables

    /** 内包している[RecyclerView] */
    val recyclerView: RecyclerView = RecyclerView(context)
    /** RecyclerView用のアダプタ */
    private val adapter: UITableViewAdapter? get() = recyclerView.adapter as? UITableViewAdapter
    /** 登録されている[CellInfo]のリスト */
    val cellInfoList = mutableListOf<CellInfo>()
    /** [UITableViewAdapter.onBindViewHolder]で返却された[View]を使い回すための一時変数 */
    var reusableCell: View? = null
    /** デリゲート */
    var delegate: UITableViewDelegate? = null
    /** データソース */
    var dataSource: UITableViewDataSource? = null
    /** セルの高さ */
    var rowHeight: CGFloat? = null
    /** セルの推定の高さ（iOS版との互換性を保つ為のプロパティであり、使用することはない） */
    @Suppress("unused")
    var estimatedRowHeight: CGFloat? = null
    /** セクション内フッターの高さ */
    var sectionFooterHeight: CGFloat? = null
    /** テーブルのヘッダー(セクション内のヘッダーではない) */
    var tableHeaderView: View? = null
        set(value) {
            field = value
            (recyclerView.adapter as? UITableViewAdapter)?.calculateTableData()
        }
    /** テーブルのフッター(セクション内のフッターではない) */
    var tableFooterView: View? = null
        set(value) {
            field = value
            (recyclerView.adapter as? UITableViewAdapter)?.calculateTableData()
        }
    /** セルをタップした際のエフェクトが有効かどうか */
    var isTouchEffectEnabled: Boolean = true
        set(value) {
            field = value
            recyclerView.adapter?.notifyDataSetChanged()
        }
    /** 画面に表示されているセル一覧を返す */
    val visibleCells: List<UITableViewCell>
        get() {
            val manager = (recyclerView.layoutManager as? LinearLayoutManager) ?: return listOf()
            val first = manager.findFirstVisibleItemPosition()
            val last = manager.findLastVisibleItemPosition()
            return (first..last).mapNotNull {
                recyclerView.findViewHolderForLayoutPosition(it)?.itemView as? UITableViewCell
            }
        }

    /** タッチの排他時間 */
    var touchExclusiveDuration: Long? = null

    // endregion

    // region -> Data class

    /** 登録されているセルクラスを管理する為の構造体 */
    data class CellInfo(val resourceId: Int, val type: KClass<*>, val isTargetIndex: TargetCheck? = null) {
        fun create(inflater: LayoutInflater, parent: ViewGroup?): View {
            return inflater.inflate(resourceId, parent, false)
        }
    }

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context) {
        commonInit(context = context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        commonInit(context = context, attrs = attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        commonInit(context = context, attrs = attrs)
    }

    // endregion

    // region -> Initializer

    private fun commonInit(context: Context, attrs: AttributeSet? = null) {
        // プロパティの読み込み
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.UITableView)
            allowsSelection = array.getBoolean(R.styleable.UITableView_allowsSelection, true)
            separatorColor = array.getColor(R.styleable.UITableView_separatorColor, separatorColor)
            array.recycle()
        }
        addView(recyclerView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = UITableViewAdapter(this)
    }

    /**
     * 罫線表示用のデコレーションアイテムを生成して返す
     * ※罫線の表示をカスタマイズしたい場合は[Override]して使用する
     */
    open fun createDividerItemDecoration(): DividerItemDecoration {
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        val shapeDrawable = ShapeDrawable(RectShape())
        shapeDrawable.intrinsicHeight = 1
        shapeDrawable.paint.color = separatorColor
        dividerItemDecoration.setDrawable(shapeDrawable)
        return dividerItemDecoration
    }

    // endregion

    // region -> Register

    /**
     * セルのリソースIDとクラスを登録する
     */
    fun <T : UITableViewCell> register(cellResourceId: Int, cellClass: KClass<T>, isTargetIndex: TargetCheck? = null) {
        val info = CellInfo(cellResourceId, cellClass, isTargetIndex)
        cellInfoList.add(info)
        if (1 < cellInfoList.size && cellInfoList.contains { it.isTargetIndex == null }) {
            throw AssertionError("when you register multi cell classes, you must set `isTargetIndex` argument.")
        }
    }

    /**
     * 指定された[IndexPath]に対応するセルを返す
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : UITableViewCell> registeredCell(indexPath: IndexPath, type: KClass<T>): T {
        val cell = dequeueReusableCell(type, indexPath)
        cell.intTag = indexPath.row
        return (cell as T)
    }

    /**
     * 使い回す対象[View]を返す
     */
    private fun <T : UITableViewCell> dequeueReusableCell(@Suppress("UNUSED_PARAMETER") type: KClass<T>, indexPath: IndexPath): UITableViewCell {
        val cell = reusableCell as? UITableViewCell
        if (cell != null) {
            return cell
        } else {
            val position = adapter?.positionBy(indexPath) ?: return UITableViewCell(context)
            return recyclerView.findViewHolderForAdapterPosition(position)?.itemView as? UITableViewCell ?: UITableViewCell(context)
        }
    }

    /**
     * 指定されたクラスからセルのリソースIDを生成して返す
     */
    protected fun resourceId(type: KClass<*>): Int {
        val fullName = type.java.simpleName
        val str = fullName.toSnakeCase()
        val id = resources.getIdentifier(str, "layout", context.packageName)
        if (id == 0) {
            throw Resources.NotFoundException("Layout file not found!!! file name is [$str.xml]")
        }
        return id
    }

    // endregion

    // region -> Action

    /**
     * セルがタップされた際に呼ばれる処理
     */
    fun onItemClick(v: View?) {
        val view = v ?: return
        if (allowsSelection && TouchLock.canTouch) {
            val position = recyclerView.getChildAdapterPosition(view)
            val indexPath = adapter?.indexPathBy(position) ?: return
            delegate?.didSelectRow(this, indexPath = indexPath)


            val duration = touchExclusiveDuration
            if (duration != null && 0 < duration) {
                TouchLock.lock(duration)
            }
        }
    }

    // endregion

    // region -> Support

    /**
     * 一覧の表示を更新する
     */
    open fun reloadData() {
        adapter?.calculateTableData()
        adapter?.notifyDataSetChanged()
    }

    fun deselectRow(@Suppress("UNUSED_PARAMETER") at: Any, @Suppress("UNUSED_PARAMETER") animated: Boolean) {}

    /**
     * 指定されたセルに対応する[IndexPath]を返す
     */
    fun indexPathFor(cell: UITableViewCell): UITableViewAdapter.CellIndexPath? {
        val position = recyclerView.getChildAdapterPosition(cell)
        return adapter?.indexPathBy(position)
    }

    // endregion
}