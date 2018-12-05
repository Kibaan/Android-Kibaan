package kibaan.sample.screen.table

import android.view.View
import kibaan.controller.BaseViewController
import kibaan.extension.stringValue
import kibaan.ios.*
import kibaan.sample.R
import kibaan.ui.SmartTableView
import kibaan.ui.SmartTextView

/**
 * UITableView関連の動作をチェックする為のコントローラ
 */
class SampleTableViewController : BaseViewController(), UITableViewDataSource, UITableViewDelegate {

    var count = 0

    // region -> Outlets

    @IBOutlet(R.id.count_text) lateinit var countText: SmartTextView
    @IBOutlet(R.id.table_view) lateinit var tableView: SmartTableView

    // endregion


    // region -> Life cycle

    override fun viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = this
        tableView.dataSource = this
        tableView.registerCellClass(SampleTableViewCell::class)
        tableView.rowHeight = 44.0
        tableView.estimatedRowHeight = tableView.rowHeight
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