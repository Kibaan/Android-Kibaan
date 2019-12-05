package kibaan.android.sample.screen.table

import android.view.View
import android.widget.Switch
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.*
import kibaan.android.sample.R
import kibaan.android.ui.SmartTableView
import kibaan.android.ui.ToastPopup

/**
 * UITableView関連の動作をチェックする為のコントローラ
 */
class TableDragViewController : SmartViewController(), UITableViewDataSource, UITableViewDelegate {

    @IBOutlet(R.id.editSwitch) lateinit var editSwitch: Switch

    @IBOutlet(R.id.longPressSwitch) lateinit var longPressSwitch: Switch

    @IBOutlet(R.id.tableView) lateinit var tableView: SmartTableView

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

    @IBAction(R.id.editSwitch)
    fun actionEditSwitch(sender: View) {
        tableView.isEditing = editSwitch.isChecked
    }

    @IBAction(R.id.longPressSwitch)
    fun actionLongPressSwitch(sender: View) {
        ToastPopup.show("LongPress")
    }

    override fun numberOfRows(tableView: UITableView, section: Int): Int {
        return 50
    }

    override fun cellForRow(tableView: UITableView, indexPath: IndexPath): View {
        val tableView = tableView as? SmartTableView ?: return UITableViewCell(tableView.context)
        val cell = tableView.registeredCell(indexPath, SampleTableViewCell::class)
        cell.index = indexPath.row
        return cell
    }
}