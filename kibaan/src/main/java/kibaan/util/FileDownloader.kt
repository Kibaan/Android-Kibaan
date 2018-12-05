package kibaan.util

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.net.Uri

internal class FileDownloader {

    private var downloadId: Long = 0
    private var listener: FileDownloader.Listener? = null

    fun start(context: Context, urlString: String) {
        // 通信不可の場合はすぐに失敗通知する
        if (!OSUtils.isNetworkEnabled(context)) {
            listener?.onFailed(urlString)
            return
        }
        //    http://y-anz-m.blogspot.jp/2010/12/androidandroid23-downloadmanager.html
        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request: DownloadManager.Request
        try {
            request = DownloadManager.Request(Uri.parse(urlString))
        } catch (e: IllegalArgumentException) {
            listener?.onFailed(urlString)
            return
        }
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        // 通知領域から見えるかどうか
        request.setNotificationVisibility(VISIBILITY_VISIBLE)
        // ダウンロードアプリから見えるかどうか
        request.setVisibleInDownloadsUi(false)
        downloadId = downloadManager.enqueue(request)
    }

    fun stop(context: Context) {
        if (downloadId == 0L) {
            return
        }
        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.remove(downloadId)
    }

    fun makeReceiver(): BroadcastReceiver {
        //        http://blog.vogella.com/2011/06/14/android-downloadmanager-example/
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                    if (downloadId != this@FileDownloader.downloadId) {
                        return
                    }
                    val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query()
                    query.setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val indexForStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = cursor.getInt(indexForStatus)
                        if (DownloadManager.STATUS_SUCCESSFUL == status) {
                            val indexForSavedURI = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                            val uriString = cursor.getString(indexForSavedURI)
                            listener?.onDownloaded(Uri.parse(uriString))
                            this@FileDownloader.downloadId = 0
                        } else if (DownloadManager.STATUS_FAILED == status) {
                            val indexForSavedURI = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                            val uriString = cursor.getString(indexForSavedURI)
                            listener?.onFailed(urlString = uriString)
                        }
                    }
                    cursor.close()
                }
            }
        }
    }

    fun setListener(listener: FileDownloader.Listener) {
        this.listener = listener
    }


    internal interface Listener {
        fun onDownloaded(uri: Uri)
        fun onFailed(urlString: String)
    }
}
