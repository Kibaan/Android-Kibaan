package kibaan.android.sample.screen.top

import android.view.View
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.*
import kibaan.android.sample.R
import kibaan.android.sample.screen.button.ButtonEventViewController
import kibaan.android.sample.screen.button.ButtonViewController
import kibaan.android.sample.screen.connection.ConnectionViewController
import kibaan.android.sample.screen.other.PagerCheckViewController
import kibaan.android.sample.screen.other.SegmentCheckViewController
import kibaan.android.sample.screen.other.TextFieldCheckViewController
import kibaan.android.sample.screen.page.FirstPageViewController
import kibaan.android.sample.screen.slider.SliderCheckViewController
import kibaan.android.sample.screen.sub.SubViewController
import kibaan.android.sample.screen.table.SampleTableViewController
import kibaan.android.ui.SmartTableView
import kotlin.reflect.KClass

/**
 * トップ画面
 */
class TopViewController : SmartViewController(), UITableViewDelegate, UITableViewDataSource {

    enum class Screen {
        PAGE, CONNECTION, TABLE, BUTTON, BUTTON_EVENT, SUB, SEGMENT, TEXT_FIELD, PAGER, SLIDER;

        val text: String
            get() {
                return when (this) {
                    PAGE -> "画面遷移確認"
                    CONNECTION -> "通信確認"
                    TABLE -> "テーブル確認"
                    BUTTON -> "ボタン確認"
                    BUTTON_EVENT -> "ボタンイベント"
                    SUB -> "サブ画面"
                    SEGMENT -> "セグメント確認"
                    TEXT_FIELD -> "テキストフィールド確認"
                    PAGER -> "ページャー確認"
                    SLIDER -> "スライダー確認"
                }
            }
        val type: KClass<out SmartViewController>
            get() {
                return when (this) {
                    PAGE -> FirstPageViewController::class
                    CONNECTION -> ConnectionViewController::class
                    TABLE -> SampleTableViewController::class
                    BUTTON -> ButtonViewController::class
                    BUTTON_EVENT -> ButtonEventViewController::class
                    SUB -> SubViewController::class
                    SEGMENT -> SegmentCheckViewController::class
                    TEXT_FIELD -> TextFieldCheckViewController::class
                    PAGER -> PagerCheckViewController::class
                    SLIDER -> SliderCheckViewController::class
                }
            }
        val id: String?
            get() {
                return when (this) {
                    PAGE -> "test_0"
                    else -> null
                }
            }
    }

    @IBOutlet(R.id.tableView)
    lateinit var tableView: SmartTableView

    override val nextScreenContainer: View get() = view

    override fun viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = this
        tableView.dataSource = this
    }

    override fun onEnterForeground() {
        super.onEnterForeground()
        tableView.reloadData()
    }

    override fun numberOfRows(tableView: UITableView, section: Int): Int {
        return Screen.values().size
    }

    @Suppress("NAME_SHADOWING")
    override fun cellForRow(tableView: UITableView, indexPath: IndexPath): View {
        val tableView = tableView as? SmartTableView ?: return UITableViewCell(context)
        val cell = tableView.registeredCell(indexPath, UITableViewCell::class)
        cell.backgroundColor = UIColor(0xFF555555)
        cell.textLabel?.textColor = UIColor.white
        cell.textLabel?.text = Screen.values()[indexPath.row].text
        return cell
    }

    override fun didSelectRow(tableView: UITableView, indexPath: IndexPath) {
        val screen = Screen.values()[indexPath.row]
        addNextScreen(screen.type, id = screen.id)
    }
}