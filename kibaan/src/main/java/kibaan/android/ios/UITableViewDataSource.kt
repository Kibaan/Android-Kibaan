package kibaan.android.ios

import android.view.View

interface UITableViewDataSource {
    fun numberOfSection(tableView: UITableView): Int {
        return 1
    }

    fun numberOfRows(tableView: UITableView, section: Int): Int
    fun cellForRow(tableView: UITableView, indexPath: IndexPath): View
    fun titleForHeaderInSection(tableView: UITableView, section: Int): String? {
        return null
    }

    fun titleForFooterInSection(tableView: UITableView, section: Int): String? {
        return null
    }

    fun moveRow(tableView: UITableView, sourceIndexPath: IndexPath, destinationIndexPath: IndexPath) {}
}