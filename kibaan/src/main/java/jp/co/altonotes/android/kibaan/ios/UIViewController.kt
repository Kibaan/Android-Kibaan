package jp.co.altonotes.android.kibaan.ios

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import butterknife.ButterKnife
import jp.co.altonotes.android.kibaan.extension.toSnakeCase
import jp.co.altonotes.android.kibaan.util.Log

/**
 * iOSのUIViewControllerの再現
 *
 * Created by yamamoto on 2018/05/15.
 */
open class UIViewController(layoutName: String? = null) {

    companion object {
        // Context（Activity）はstaticに保持する
        @SuppressLint("StaticFieldLeak")
        private var activity: AppCompatActivity? = null

        fun setActivity(activity: AppCompatActivity?) {
            Companion.activity = activity
        }
    }

    protected val context: Context?
        get() = activity


    private var _view: ViewGroup? = null
    val view: ViewGroup
        get() {
            if (!_isViewLoaded) {
                _isViewLoaded = true
                // viewDidLoadは最初にviewが参照されたとき呼び出す
                viewDidLoad()
            }
            return _view!!
        }

    private var _isViewLoaded = false
    val isViewLoaded: Boolean
        get() = _isViewLoaded

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
            viewDidAppear(false)
            view.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
    }

    // OnLayoutChangeListenerでviewDidLayoutSubviewsを呼び出す
    private val layoutChangeListener: View.OnLayoutChangeListener = View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        viewDidLayoutSubviews()
    }

    init {
        loadLayout(layoutName)
    }

    // レイアウトファイルを読み込み、Viewを作成する
    private fun loadLayout(layoutName: String? = null) {

        val context = activity ?: return

        val layoutName = layoutName ?: javaClass.simpleName
        val resourceName = layoutName.toSnakeCase()
        val layoutId = context.resources.getIdentifier(resourceName, "layout", context.packageName)

        try {
            val loadedView = LayoutInflater.from(context).inflate(layoutId, null, false)
            _view = loadedView as ViewGroup
        } catch (e: Resources.NotFoundException) {
            Log.e(javaClass.simpleName, "Layout file not found!!! file name is [$resourceName]")
            throw e
        }

        val view = _view ?: return

        //ButterKnifeでbindする
        ButterKnife.bind(this, view)

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