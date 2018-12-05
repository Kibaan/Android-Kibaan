package kibaan.ios

import android.view.View

/**
 * テーブルビューのデリゲート
 */
interface UITableViewDelegate {
    fun viewForHeaderInSection(tableView: UITableView, section: Int): View? {
        return null
    }

    fun viewForFooterInSection(tableView: UITableView, section: Int): View? {
        return null
    }

    fun heightForHeaderInSection(tableView: UITableView, section: Int): CGFloat? {
        return null
    }

    fun heightForFooterInSection(tableView: UITableView, section: Int): CGFloat? {
        return null
    }

    fun heightForRowAt(tableView: UITableView, indexPath: IndexPath): CGFloat? {
        return null
    }

    fun willDisplayCell(indexPath: IndexPath) {}
    fun didSelectRow(tableView: UITableView, indexPath: IndexPath) {}

}
