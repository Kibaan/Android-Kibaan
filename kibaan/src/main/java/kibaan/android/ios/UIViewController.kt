package kibaan.android.ios

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import butterknife.ButterKnife
import kibaan.android.extension.toSnakeCase
import kibaan.android.util.Log

/**
 * iOSのUIViewControllerの再現
 *
 * Created by yamamoto on 2018/05/15.
 */
open class UIViewController(layoutName: String? = null) {

    companion object {
        // Context（Activity）はstaticに保持する
        @SuppressLint("StaticFieldLeak")
        private var globalContext: Context? = null

        fun setGlobalContext(context: Context?) {
            globalContext = context
        }
    }

    protected val context: Context
        get() = view.context


    private lateinit var privateView: ViewGroup
    val view: ViewGroup
        get() {
            if (!privateIsViewLoaded) {
                privateIsViewLoaded = true
                // viewDidLoadは最初にviewが参照されたとき呼び出す
                viewDidLoad()
            }
            return privateView
        }

    private var privateIsViewLoaded = false
    val isViewLoaded: Boolean
        get() = privateIsViewLoaded

    // OnAttachStateChangeListenerでviewWillAppear、viewWillDisappear、viewDidDisappearを呼び出す
    private val attachListener: View.OnAttachStateChangeListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View?) {
            viewWillAppear(false)
            view?.viewTreeObserver?.addOnGlobalLayoutListener(layoutListener)
        }

        override fun onViewDetachedFromWindow(view: View?) {
            viewWillDisappear(false)
            viewDidDisappear(false)
        }
    }

    // OnGlobalLayoutListenerでviewDidAppearを呼び出す
    private val layoutListener: ViewTreeObserver.OnGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            view.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            viewDidAppear(false)
        }
    }

    // OnLayoutChangeListenerでviewDidLayoutSubviewsを呼び出す
    private val layoutChangeListener: View.OnLayoutChangeListener = View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        viewDidLayoutSubviews()
    }

    init {
        val context = globalContext ?: throw IllegalStateException("Context is not set. Call UIViewController.setGlobalContext(Context) before init.")
        loadLayout(context = context, layoutName = layoutName)
    }

    // レイアウトファイルを読み込み、Viewを作成する
    private fun loadLayout(context: Context, layoutName: String? = null) {

        val layoutName = layoutName ?: javaClass.simpleName
        val resourceName = layoutName.toSnakeCase()
        val layoutId = context.resources.getIdentifier(resourceName, "layout", context.packageName)

        try {
            val loadedView = LayoutInflater.from(context).inflate(layoutId, null, false)
            privateView = loadedView as ViewGroup
        } catch (e: Resources.NotFoundException) {
            Log.e(javaClass.simpleName, "Layout file not found!!! file name is [$resourceName]")
            throw e
        }

        //ButterKnifeでbindする
        ButterKnife.bind(this, privateView)

        view.addOnAttachStateChangeListener(attachListener)
        view.addOnLayoutChangeListener(layoutChangeListener)

        // 裏のViewにタッチが貫通しないようにする
        view.setOnTouchListener { _, _ -> true }
    }

    fun <T : ViewDataBinding> bind(): T? {
        return DataBindingUtil.bind(view)
    }

    open fun viewDidLoad() {}

    open fun viewWillAppear(animated: Boolean) {}

    open fun viewDidAppear(animated: Boolean) {}

    open fun viewWillDisappear(animated: Boolean) {}

    open fun viewDidDisappear(animated: Boolean) {}

    open fun viewDidLayoutSubviews() {}

}