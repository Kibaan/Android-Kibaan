package kibaan.android.ios

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.webkit.*


class UIWebView : WebView {

    var delegate: UIWebViewDelegate? = null

    var isLoading: Boolean = false
    var isReceivedError = false

    // region -> Constructor

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    // endregion

    init {

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setup() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // Xperia Z Tabletはこの指定をしないと落ちる
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        webViewClient = uiWebViewClient

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        if (21 <= Build.VERSION.SDK_INT) {
            // webViewのコンテンツがhttps、headタグに埋め込むcssがhttpであるため設定
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            setWebContentsDebuggingEnabled(true)
        }
    }

    private val uiWebViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            isLoading = true
            delegate?.webViewDidStartLoad(this@UIWebView)
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            isReceivedError = true
            delegate?.webViewDidFailLoad(webView = this@UIWebView, errorCode = errorCode, description = description)
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            if (request.isForMainFrame) {
                isReceivedError = true
            }
            delegate?.webViewDidFailLoad(webView = this@UIWebView, errorCode = error.errorCode, description = error.description.toString())
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            isLoading = false
            delegate?.webViewDidFinishLoad(this@UIWebView)
        }

        // shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) でURL(request.url)を参照するにはAPI Level21以上が必要となり、コンパイルエラーになるため非推奨のメソッドを使っている
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            super.shouldOverrideUrlLoading(view, url)
            val result = delegate?.webViewShouldStartLoad(this@UIWebView, url, UIWebViewNavigationType.LINK_CLICKED)
            return if (result != null) !result else false
        }

        override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
            resend?.sendToTarget()
        }
    }

    /**
     * JavaScriptを実行する
     */
    fun stringByEvaluatingJavaScript(from: String) {
        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            evaluateJavascript(from, null)
        } else {
            loadUrl("javascript:$from")
        }
    }

    fun clear() {
        clearCache(true)
        clearHistory()
    }

    override fun reload() {
        isReceivedError = false
        super.reload()
    }

    override fun goBack() {
        isReceivedError = false
        super.goBack()
    }

    override fun goForward() {
        isReceivedError = false
        super.goForward()
    }

    override fun postUrl(url: String?, postData: ByteArray?) {
        isReceivedError = false
        super.postUrl(url, postData)
    }

    override fun loadUrl(url: String?) {
        isReceivedError = false
        super.loadUrl(url)
    }

    override fun loadDataWithBaseURL(baseUrl: String?, data: String?, mimeType: String?, encoding: String?, historyUrl: String?) {
        isReceivedError = false
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }
}

interface UIWebViewDelegate {
    fun webViewShouldStartLoad(webView: UIWebView, url: String, navigationType: UIWebViewNavigationType): Boolean {
        return true
    }

    fun webViewDidStartLoad(webView: UIWebView) {}
    fun webViewDidFinishLoad(webView: UIWebView) {}
    fun webViewDidFailLoad(webView: UIWebView, errorCode: Int, description: String?) {}
}

enum class UIWebViewNavigationType {
    LINK_CLICKED,
    FORM_SUBMITTED,
    BACK_FORWARD,
    RELOAD,
    FORM_RESUBMITTED,
    OTHER
}
