package kibaan.android.sample.screen.table

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.View
import kibaan.android.framework.SmartViewController
import kibaan.android.extension.stringValue
import kibaan.android.ios.*
import kibaan.android.sample.R
import kibaan.android.ui.SmartTableView
import kibaan.android.ui.SmartTextView
import kibaan.android.util.AlertUtils

/**
 * UITableView関連の動作をチェックする為のコントローラ
 */
class SampleTableViewController : SmartViewController(), UITableViewDataSource, UITableViewDelegate {

    var count = 0

    // region -> Outlets

    @IBOutlet(R.id.count_text) lateinit var countText: SmartTextView
    @IBOutlet(R.id.table_view) lateinit var tableView: SmartTableView
    @IBOutlet(R.id.swipe_refresh_layout) lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
    }

    override fun onEnterForeground() {
        super.onEnterForeground()
        tableView.reloadData()
    }

    // endregion

    // region -> UITableViewDataSource

    override fun numberOfRows(tableView: UITableView, section: Int): Int {
        return 200
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