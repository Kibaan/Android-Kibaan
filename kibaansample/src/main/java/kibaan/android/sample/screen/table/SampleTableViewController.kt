package kibaan.android.sample.screen.table

import android.util.TypedValue
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kibaan.android.extension.stringValue
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.*
import kibaan.android.sample.R
import kibaan.android.ui.SmartTableView
import kibaan.android.ui.SmartTextView
import kibaan.android.util.AlertUtils
import kibaan.android.util.DeviceUtils

/**
 * UITableView関連の動作をチェックする為のコントローラ
 */
class SampleTableViewController : SmartViewController(), UITableViewDataSource, UITableViewDelegate {

    var count = 0
    var dataCount = 200

    // region -> Outlets

    @IBOutlet(R.id.count_text)
    lateinit var countText: SmartTextView
    @IBOutlet(R.id.table_view)
    lateinit var tableView: SmartTableView
    @IBOutlet(R.id.swipe_refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // endregion


    // region -> Life cycle

    override fun viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = this
        tableView.dataSource = this
        tableView.registerCellClass(SampleTableViewCell::class)
        tableView.rowHeight = 44.0
        tableView.estimatedRowHeight = tableView.rowHeight
        tableView.swipeRefreshLayout = swipeRefreshLayout
        tableView.addRefreshControl(swipeRefreshLayout) {
            AlertUtils.show("Success", "PullToRefresh!!")
            tableView.endRefreshing()
        }
        tableView.setNoDataMessage("データがありません。")
        tableView.contentInset = UIEdgeInsets(0, 0, DeviceUtils.toPx(view.context, 66), 0)
    }

    override fun onEnterForeground() {
        super.onEnterForeground()
        tableView.reloadData()
    }

    // endregion

    // region -> Actions

    @IBAction(R.id.changeButton)
    fun actionChangeButton(sender: View) {
        dataCount = if (dataCount == 0) 200 else 0
        tableView.reloadData()
        tableView.showNoDataLabel(dataCount == 0)
    }

    // endregion

    // region -> UITableViewDataSource

    override fun numberOfRows(tableView: UITableView, section: Int): Int {
        return dataCount
    }

//    override fun viewForHeaderInSection(tableView: UITableView, section: Int): View? {
//        val label = SmartLabel(tableView.context)
//        label.text = "viewForHeaderInSection"
//        return label
//    }

    override fun titleForHeaderInSection(tableView: UITableView, section: Int): String? {
        return "金額指定"
    }

    override fun titleForFooterInSection(tableView: UITableView, section: Int): String? {
        return "titleForFooterInSection"
    }

//    override fun willDisplayHeaderView(view: View, section: Int) {
//        val header = view as? UITableViewHeaderFooterView ?: return
//        header.borderWidth = 0.5
//        header.borderColor = UIColor.gray
//        header.textLabel?.textColor = UIColor.red
//        header.textLabel?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
//    }

    override fun willDisplayFooterView(view: View, section: Int) {
        val header = view as? UITableViewHeaderFooterView ?: return
        header.borderWidth = 0.5
        header.borderColor = UIColor.red
        header.textLabel?.textColor = UIColor.blue
        header.textLabel?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
    }

    override fun cellForRow(tableView: UITableView, indexPath: IndexPath): View {
        val tableView = tableView as? SmartTableView ?: return UITableViewCell(tableView.context)
        val cell = tableView.registeredCell(indexPath, SampleTableViewCell::class)
        return cell
    }

    // endregion

    // region -> UITableViewDelegate

    override fun didSelectRow(tableView: UITableView, indexPath: IndexPath) {
        // do nothing.
        count++
        countText.text = count.stringValue
    }

    // endregion
}