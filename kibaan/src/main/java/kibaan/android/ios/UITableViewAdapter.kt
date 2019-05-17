package kibaan.android.ios

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import kibaan.android.util.DeviceUtils
import kibaan.android.R


class UITableViewAdapter(private var tableView: UITableView) : androidx.recyclerview.widget.RecyclerView.Adapter<UITableViewAdapter.UITableViewHolder>() {

    // region -> Variables

    /** 各セクション情報の配列 */
    private var sectionInfoList: List<SectionInfo>? = null
    /** コンテキストアクセス用 */
    private val context: Context get() = tableView.context
    /** セルのレイアウトをXMLから読み込む為のインフレータ */
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    /** テーブルヘッダーがあるか */
    private val hasTableHeader: Boolean get() = tableView.tableHeaderView != null
    /** テーブルフッターがあるか */
    private val hasTableFooter: Boolean get() = tableView.tableFooterView != null
    /** 並び替え元位置 */
    var fromPos: Int? = null
    /** 並び替え先位置 */
    var toPos: Int? = null
    /** 並び替え機能を実装する際のヘルパー */
    private var itemTouchHelper: ItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN or ItemTouchHelper.UP)
        }

        override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition
            notifyItemMoved(from, to)
            return true
        }

        override fun onSelectedChanged(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.itemView?.alpha = 0.8f
            } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                val fromPos = this@UITableViewAdapter.fromPos
                val toPos = this@UITableViewAdapter.toPos
                if (fromPos != null && toPos != null) {
                    val sourceIndexPath = indexPathBy(fromPos) ?: return
                    val destinationIndexPath = indexPathBy(toPos) ?: return
                    tableView.dataSource?.moveRow(tableView, sourceIndexPath, destinationIndexPath)
                }
                this@UITableViewAdapter.fromPos = null
                this@UITableViewAdapter.toPos = null
            }
        }

        override fun canDropOver(recyclerView: androidx.recyclerview.widget.RecyclerView, current: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
            // TODO:ViewTypeが複数ある場合に位置変更アニメーションが動作しない問題がある
            return current.itemViewType == target.itemViewType
        }

        override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
            // do nothing.
        }

        override fun onMoved(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, fromPos: Int, target: androidx.recyclerview.widget.RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
            if (this@UITableViewAdapter.fromPos == null) {
                this@UITableViewAdapter.fromPos = fromPos
            }
            this@UITableViewAdapter.toPos = toPos
        }

        override fun clearView(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.alpha = 1.0f
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }
    })

    // endregion

    // region -> Initializer

    init {
        itemTouchHelper.attachToRecyclerView(tableView.recyclerView)
    }

    // endregion

    // region -> Life cycle

    /**
     * アイテムの合計数を返却する
     */
    override fun getItemCount(): Int {
        return sectionInfoList?.sumBy { it.totalCount } ?: 0
    }

    /**
     * 指定された[position]に対応するviewTypeを返却する
     */
    override fun getItemViewType(position: Int): Int {
        val indexPath = indexPathBy(position) ?: return CellType.normal.rawValue
        return when (indexPath.cellType) {
            CellType.normal -> {
                val index = tableView.cellInfoList.indexOrNull {
                    it.isTargetIndex?.invoke(indexPath) ?: true
                } ?: 0
                indexPath.cellType.rawValue + index
            }
            else -> indexPath.cellType.rawValue
        }
    }

    /**
     * [UITableViewHolder]を生成する際に呼ばれる処理
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UITableViewHolder {
        return if (CellType.isNormal(viewType)) {
            val cell: UITableViewCell
            val cellInfo = tableView.cellInfoList.safeGet(viewType - CellType.normal.rawValue)
            if (cellInfo != null) {
                cell = cellInfo.type.java.getConstructor(Context::class.java).newInstance(context) as? UITableViewCell ?: UITableViewCell(context)
                cell.contentView = cellInfo.create(inflater, parent) as? ViewGroup
            } else {
                cell = UITableViewCell(context)
                cell.contentView = FrameLayout(context)
            }
            cell.setOnClickListener { tableView.onItemClick(it) }
            cell.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    cell.setHighlighted(true, true)
                } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    cell.setHighlighted(false, true)
                }
                return@setOnTouchListener false
            }
            UITableViewHolder(cell)
        } else {
            val frameView = FrameLayout(context)
            frameView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            UITableViewHolder(frameView)
        }
    }

    private fun getRealPosition(position: Int): Int {
        val fromPos = this.fromPos ?: return position
        val toPos = this.toPos ?: return position
        if (fromPos < toPos && fromPos <= position && position < toPos) {
            return position + 1
        } else if (toPos < fromPos && toPos < position && position <= fromPos) {
            return position - 1
        }
        return position
    }

    /**
     * セルが画面に表示される際に呼ばれる処理
     */
    @Suppress("NAME_SHADOWING")
    override fun onBindViewHolder(holder: UITableViewHolder, position: Int) {
        val position = getRealPosition(position)
        val indexPath = indexPathBy(position) ?: return
        val cellType = indexPath.cellType
        when (cellType) {
            CellType.header -> {
                holder.replaceInnerView(tableView.tableHeaderView)
            }
            CellType.footer -> {
                holder.replaceInnerView(tableView.tableFooterView)
            }
            CellType.sectionHeader -> {
                val view = tableView.delegate?.viewForHeaderInSection(tableView, section = indexPath.section) ?: return
                holder.replaceInnerView(view)
                val height = tableView.delegate?.heightForHeaderInSection(tableView, section = indexPath.section) ?: return
                updateCellHeight(holder.itemView, dpHeight = height)
            }
            CellType.sectionFooter -> {
                val view = tableView.delegate?.viewForFooterInSection(tableView, section = indexPath.section) ?: return
                holder.replaceInnerView(view)
                val height = tableView.delegate?.heightForFooterInSection(tableView, section = indexPath.section) ?: return
                updateCellHeight(holder.itemView, dpHeight = height)
            }
            else -> {
                val cell = holder.itemView as? UITableViewCell
                val rowHeight = tableView.delegate?.heightForRowAt(tableView, indexPath = indexPath) ?: tableView.rowHeight
                if (rowHeight != null && rowHeight != UITableViewAutomaticDimension && cell != null) {
                    updateCellHeight(cell, dpHeight = rowHeight)
                }
                setTouchEffect(cell)
                tableView.reusableCell = cell
                holder.draggableListener = if (tableView.isEditing) this else null
                cell?.accessoryView = if (tableView.isEditing) {
                    holder.sortEditView
                } else when (cell?.accessoryType) {
                    UITableViewCell.AccessoryType.disclosureIndicator -> cell.disclosureIndicatorView
                    UITableViewCell.AccessoryType.checkmark -> cell.checkMarkView
                    else -> null
                }
                tableView.delegate?.willDisplayCell(indexPath = indexPath)
                tableView.dataSource?.cellForRow(tableView, indexPath = indexPath)
                tableView.reusableCell = null
            }
        }
    }

    /**
     * セルをタップした際のエフェクトを設定する
     */
    private fun setTouchEffect(cell: UITableViewCell?) {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            if (tableView.isTouchEffectEnabled) {
                val outValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                cell?.foreground = ContextCompat.getDrawable(context, outValue.resourceId)
            } else {
                cell?.foreground = null
            }
        }
    }

    // endregion

    // region -> Support

    /**
     * 各セクションの表示データを再計算する
     */
    fun calculateTableData() {
        sectionInfoList = calcSectionInfoList()
    }

    /**
     * 指定された[position]から[IndexPath]を計算して返す.
     */
    internal fun indexPathBy(position: Int): CellIndexPath? {
        return sectionInfoList?.firstOrNull {
            it.contains(position)
        }?.indexPathBy(position)
    }

    /**
     * 指定された[indexPath]からポジションを計算して返す
     */
    internal fun positionBy(indexPath: IndexPath): Int {
        return sectionInfoList?.safeGet(indexPath.section)?.let { sectionInfo ->
            return sectionInfo.startPosition + indexPath.row + sectionInfo.headerCount
        } ?: 0
    }

    // endregion

    // region -> Private

    /**
     * 指定された[view]の高さを[dpHeight]に更新する
     */
    private fun updateCellHeight(view: View?, dpHeight: CGFloat) {
        val pxHeight = if (dpHeight == UITableViewAutomaticDimension) ViewGroup.LayoutParams.WRAP_CONTENT else DeviceUtils.toPx(context, dpHeight)
        if (view?.layoutParams != null) {
            view.layoutParams?.height = pxHeight
        } else {
            view?.layoutParams = androidx.recyclerview.widget.RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxHeight)
        }
    }

    /**
     * 各セクションの表示件数などを計算する
     */
    private fun calcSectionInfoList(): List<SectionInfo> {
        val list = mutableListOf<SectionInfo>()
        var startPosition = 0
        if (hasTableHeader) {
            list.add(SectionInfo(null, true, false, 0, startPosition))
            startPosition += 1
        }
        val sectionCount = tableView.dataSource?.numberOfSection(tableView) ?: 1
        (0 until sectionCount).forEach {
            val rowCount = (tableView.dataSource?.numberOfRows(tableView, section = it) ?: 0)
            val hasSectionHeader = hasHeaderOfSection(section = it)
            val hasSectionFooter = hasFooterOfSection(section = it)
            val sectionInfo = SectionInfo(it, hasSectionHeader, hasSectionFooter, rowCount, startPosition)
            list.add(sectionInfo)
            startPosition = sectionInfo.endPosition + 1
        }
        if (hasTableFooter) {
            list.add(SectionInfo(null, false, true, 0, startPosition))
        }
        return list
    }

    /**
     * 指定された[section]にヘッダーがあるかどうかを返す
     */
    private fun hasHeaderOfSection(section: Int): Boolean {
        val view = tableView.delegate?.viewForHeaderInSection(tableView, section = section)
        val viewHeight = tableView.delegate?.heightForHeaderInSection(tableView, section = section)
        // TODO:sectionHeaderHeightを追加
        return (view != null || (viewHeight != null && viewHeight != CGFloat.leastNormalMagnitude && viewHeight != UITableViewAutomaticDimension))
    }

    /**
     * 指定された[section]にフッターがあるかどうかを返す
     */
    private fun hasFooterOfSection(section: Int): Boolean {
        val view = tableView.delegate?.viewForFooterInSection(tableView, section = section)
        var viewHeight = tableView.sectionFooterHeight ?: Double.leastNormalMagnitude
        val heightOfSection = tableView.delegate?.heightForFooterInSection(tableView, section = section)
        if (heightOfSection != null && heightOfSection != Double.leastNormalMagnitude) {
            viewHeight = heightOfSection
        }
        return (view != null || (viewHeight != CGFloat.leastNormalMagnitude && viewHeight != UITableViewAutomaticDimension))
    }

    // endregion

    // region -> DraggableListener

    /**
     * ドラッグ処理を開始する際に呼ばれる処理
     */
    fun onStartDrag(holder: UITableViewHolder) {
        itemTouchHelper.startDrag(holder)
    }

    // endregion

    // region -> Inner class

    @SuppressLint("ClickableViewAccessibility")
    open class UITableViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        var draggableListener: UITableViewAdapter? = null

        /** 並び替え用のView */
        open val sortEditView: ImageView? by lazy {
            val imageView = ImageView(itemView.context)
            val padding = DeviceUtils.toPx(itemView.context, 6)
            imageView.setImageResource(R.drawable.sort_edit)
            imageView.setPadding(padding, padding, padding, padding)
            val listener = View.OnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        draggableListener?.onStartDrag(this)
                    }
                }
                false
            }
            imageView.setOnTouchListener(listener)
            return@lazy imageView
        }

        /**
         * itemViewの中身を指定された[view]に入れ替える
         */
        fun replaceInnerView(view: View?) {
            val viewGroup = (itemView as? ViewGroup)
            viewGroup?.removeAllViews()
            viewGroup?.addView(view, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        }
    }

    class CellIndexPath(val cellType: CellType = CellType.normal, section: Int?, row: Int) : IndexPath(section ?: -1, row)

    enum class CellType : IntEnumDefault {
        header, sectionHeader, footer, sectionFooter, normal;

        companion object {
            fun isNormal(viewType: Int): Boolean {
                return normal.rawValue <= viewType
            }
        }
    }

    internal data class SectionInfo(var section: Int?, var hasHeader: Boolean, private var hasFooter: Boolean, private var rowCount: Int, val startPosition: Int) {

        val totalCount: Int get() = rowCount + headerCount + footerCount
        val endPosition: Int get() = startPosition + totalCount - 1
        val headerCount: Int get() = if (hasHeader) 1 else 0
        private val footerCount: Int get() = if (hasFooter) 1 else 0

        fun contains(position: Int): Boolean {
            return position in startPosition..endPosition
        }

        fun indexPathBy(position: Int): CellIndexPath {
            val row = position - startPosition - headerCount
            val cellType = when {
                (section == null && hasHeader) -> CellType.header
                (section == null && hasFooter) -> CellType.footer
                (hasHeader && row == -headerCount) -> CellType.sectionHeader
                (hasFooter && endPosition == position) -> CellType.sectionFooter
                else -> CellType.normal
            }
            return CellIndexPath(cellType, section, row)
        }
    }

    // endregion
}
