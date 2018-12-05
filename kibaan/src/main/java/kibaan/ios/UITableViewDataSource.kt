package kibaan.ios

import android.view.View

interface UITableViewDataSource {
    fun numberOfSection(tableView: UITableView): Int {
        return 1
    }

    fun numberOfRows(tableView: UITableView, section: Int): Int
    fun cellForRow(tableView: UITableView, indexPath: IndexPath): View
    fun moveRow(tableView: UITableView, sourceIndexPath: IndexPath, destinationIndexPath: IndexPath) {}
}