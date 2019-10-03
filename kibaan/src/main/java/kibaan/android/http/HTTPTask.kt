package kibaan.android.http

import android.os.Handler
import android.view.View
import kibaan.android.AndroidUnique
import kibaan.android.ios.*
import kibaan.android.task.Task
import kibaan.android.task.TaskHolder
import kibaan.android.util.Log
import kibaan.android.util.QueryUtils
import kibaan.android.valueobject.KeyValue
import java.nio.charset.Charset


abstract class HTTPTask : Task {

    companion object {
        /** 共通のUser-Agent */
        var defaultUserAgent: String? = null
        /** 共通のHTTPConnector */
        var createHttpConnector: () -> HTTPConnector = {
            HTTPConnectorImpl()
        }
    }

    /** 通信インジケーター */
    var indicator: View? = null
    /** データ転送のタイムアウト時間 */
    open var timeoutIntervalForRequest: TimeInterval = 30.0
    /** 通信完了までのタイムアウト時間 */
    open var timeoutIntervalForResource: TimeInterval = 60.0
    /** 通信オブジェクト */
    open var httpConnector: HTTPConnector = createHttpConnector()

    /** クエリパラメーター */
    private var queryItems: MutableList<KeyValue> = mutableListOf()

    /** リクエスト内容をログ出力するか */
    open val isRequestLogEnabled: Boolean get() = true
    /** レスポンス内容をログ出力するか */
    open val isResponseLogEnabled: Boolean get() = false

    @AndroidUnique
    val handler = Handler()

    constructor() : super()
    constructor(owner: TaskHolder, key: String?) : super(owner, key)

    /**
     * 通信を開始する
     */
    override fun start() {
        var urlStr = requestURL

        if (isRequestLogEnabled) {
            Log.d( javaClass.simpleName,"[$httpMethod] $urlStr")
        }

        prepareRequest()

        // クエリを作成
        var httpBody: ByteArray? = null
        if (httpMethod == "POST") {
            httpBody = makePostData()
        } else {
            val query = makeQueryString()
            if (query != null) {
                urlStr += "?$query"
            }
        }

        val request = Request(urlStr)
        request.httpMethod = httpMethod
        request.body = httpBody

        val headers = this.headers
        val userAgent = defaultUserAgent
        if (userAgent != null && !headers.keys.map { it.lowercased() }.contains("user-agent")) {
            request.headers["User-Agent"] = userAgent
        }
        @AndroidUnique
        // ボディがあってcontent-typeが空の場合はデフォルトのcontent-typeをセット
        if (httpBody != null && !headers.keys.map { it.lowercased() }.contains("content-type")) {
            request.headers["Content-Type"] = "application/x-www-form-urlencoded"
        }
        for ((key, value) in headers) {
            request.headers[key] = value
        }

        httpConnector.timeoutIntervalForRequest = timeoutIntervalForRequest
        httpConnector.timeoutIntervalForResource = timeoutIntervalForResource

        httpConnector.execute(request, completionHandler = {data, response, error ->
            complete(data, response, error)
        })

        updateIndicator(referenceCount = 1)
    }

    /**
     * 通信完了時の処理
     */
    private fun complete(data: ByteArray?, response: HTTPURLResponse?, error: Exception?) {
        try {
            if (httpConnector.isCancelled) {
                return
            }

            if (response == null) {
                handleConnectionError(HTTPTaskError.network, error = error, response = response)
                return
            }
            // 通信エラーをチェック
            if (error != null) {
                handleConnectionError(HTTPTaskError.network, error = error, response = response)
                return
            }
            if (data == null) {
                handleConnectionError(HTTPTaskError.network, error = error, response = response)
                return
            }
            // ステータスコードをチェック
            if (!checkStatusCode(response.statusCode)) {
                statusCodeError(response = response, data = data)
                return
            }
            handleResponseData(data = data, response = response)

        } finally {
            handler.post {
                updateIndicator(referenceCount = -1)
                finishHandler?.invoke()
                end()
            }
        }
    }

    /**
     * レスポンスデータを処理する
     */
    open fun handleResponseData(data: ByteArray, response: HTTPURLResponse) {}

    /**
     * 通信エラーを処理する
     */
    open fun handleConnectionError(type: HTTPTaskError, error: Exception? = null, response: HTTPURLResponse? = null, data: ByteArray? = null) {
    }

    /**
     * ステータスコードエラーを処理する
     */
    open fun statusCodeError(response: HTTPURLResponse?, data: Data?) {
    }

    /**
     * 通信をキャンセルする
     */
    override fun cancel() {
        super.cancel()
        httpConnector.cancel()
    }

    // region -> Computed Property

    /** 通信先URL */
    open val requestURL: String get() = baseURL + path
    /** 基本通信先URL */
    open val baseURL: String get() = ""
    /** 通信先URLパス */
    open val path: String get() = ""
    /** リクエストHTTPメソッド */
    open val httpMethod: String get() = "GET"
    /** リクエストヘッダー */
    open val headers: Map<String, String> get() = mapOf()
    /** リクエストエンコーディング */
    open val requestEncoding: Charset get() = Charsets.UTF_8

    // endregion

    /**
     * リクエストを作成する
     */
    open fun prepareRequest() {
        // Override
    }

    /**
     * ステータスコードが有効か判定する
     */
    open fun checkStatusCode(code: Int): Boolean = code == 200

    /**
     * 通信インジケーターを更新する。referenceCountは通信開始時に+1、通信完了時に-1を指定する
     */
    open fun updateIndicator(referenceCount: Int) {
        val view = indicator
        if (view != null) {
            view.intTag += referenceCount
            view.isGone = (view.intTag <= 0)
        }
    }

    /**
     * クエリパラメーターを追加する
     */
    fun addQuery(key: String, value: String?) {
        val item = KeyValue(key = key, value = value)
        queryItems.append(item)
    }

    open fun makePostData(): ByteArray? {
        val query = makeQueryString() ?: return null
        return query.data(using = requestEncoding)
    }

    open fun makeQueryString(): String? {
        val query = QueryUtils.makeQueryString(items = queryItems, encoder = parameterEncoder) ?: return null
        if (isRequestLogEnabled) {
            Log.d( javaClass.simpleName,"      > $query")
        }
        return query
    }

    open val parameterEncoder: ((String) -> (String))? get() = null

    /**
     * エラー処理状態
     */
    enum class ErrorHandlingStatus {
        finish,
        handleError
    }
}