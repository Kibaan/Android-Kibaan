package jp.co.altonotes.android.kibaan.util

import android.support.v7.app.AlertDialog
import jp.co.altonotes.android.kibaan.controller.SmartActivity
import jp.co.altonotes.android.kibaan.ios.append
import jp.co.altonotes.android.kibaan.ios.removeAll

/**
 * Alertのユーティリティー
 * Created by yamamoto on 2018/02/09.
 */
object AlertUtils {

    var isEnabled = true

    private var displayingList: MutableList<AlertDialog> = mutableListOf()

    fun showNotice(title: String, message: String) {
        show(title = title, message = message)
    }

    fun showErrorNotice(message: String) {
        show(title = "エラー", message = message)
    }

    fun showErrorNotice(message: String, handler: (() -> Unit)) {
        show(title = "エラー", message = message, handler = handler)
    }

    fun show(title: String, message: String, okLabel: String = "閉じる",
             handler: (() -> Unit)? = null, cancelLabel: String? = null, cancelHandler: (() -> Unit)? = null): AlertDialog? {

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
            it.dismiss()
        }
        displayingList.removeAll()
    }
}