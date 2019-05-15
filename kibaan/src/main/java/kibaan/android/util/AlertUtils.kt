package kibaan.android.util

import androidx.appcompat.app.AlertDialog
import kibaan.android.framework.SmartActivity
import kibaan.android.ios.append
import kibaan.android.ios.removeAll

/**
 * Alertのユーティリティー
 * Created by yamamoto on 2018/02/09.
 */
object AlertUtils {

    var isEnabled = true
    var defaultErrorTitle = "エラー"
    var defaultCloseLabel = "閉じる"

    private var displayingList: MutableList<AlertDialog> = mutableListOf()

    /**
     * 閉じるだけのアラートを表示する
     */
    fun showNotice(title: String, message: String, handler: (() -> Unit)? = null) {
        show(title = title, message = message)
    }

    /**
     * 閉じるだけのエラーアラートを表示する
     */
    fun showErrorNotice(message: String, handler: (() -> Unit)? = null) {
        show(title = defaultErrorTitle, message = message, handler = handler)
    }

    fun show(title: String,
             message: String,
             okLabel: String = defaultCloseLabel,
             handler: (() -> Unit)? = null,
             cancelLabel: String? = null,
             cancelHandler: (() -> Unit)? = null): AlertDialog? {

        if (!isEnabled) return null
        val activity = SmartActivity.sharedOrNull ?: return null

        val builder = AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okLabel) { _, _ ->
                    handler?.invoke()
                }
                .setCancelable(false)

        if (cancelLabel != null) {
            builder.setNegativeButton(cancelLabel) { _, _ ->
                cancelHandler?.invoke()
            }
        }
        val dialog = builder.create()
        displayingList.append(dialog)
        dialog.setOnDismissListener {
            displayingList.remove(dialog)
        }
        dialog.show()
        return dialog
    }

    fun clear() {
        isEnabled = true
    }

    fun dismissAllAlert() {
        displayingList.forEach {
            try {
                it.dismiss()
            } catch (e: Exception) {
                // ダイアログを表示していたアクティビティが終了済みの場合、dismissでExceptionが発生するのでキャッチする
            }
        }
        displayingList.removeAll()
    }
}