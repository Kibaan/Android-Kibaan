package kibaan.android.framework

import android.animation.Animator
import android.os.Build
import android.view.View
import android.view.ViewGroup
import kibaan.android.task.TaskHolder
import kibaan.android.ios.*
import kotlin.reflect.KClass

/**
 *
 * Created by Yamamoto Keita on 2018/01/18.
 */

open class BaseViewController(layoutName: String? = null) : UIViewController(layoutName) {

    /** 同クラスのインスタンスが複数存在する場合に識別するためのID */
    var viewID: String = ""
    /** 子のViewController */
    private var subControllers = mutableListOf<BaseViewController>()
    /** 表示中の子ViewControllerの配列 */
    open val foregroundSubControllers: List<BaseViewController> get() = listOf()
    /** 表示中のViewController */
    val foregroundController: BaseViewController get() = nextScreens.lastOrNull() ?: this
    /** 紐づくタスクのコンテナ */
    var taskHolder = TaskHolder()
    /** 上に乗せたオーバーレイ画面 */
    private var overlays = mutableListOf<BaseViewController>()
    /** オーバーレイ画面が乗っているか */
    val hasOverlay: Boolean get() = !overlays.isEmpty()
    /** スライド表示させた画面リスト */
    private var nextScreens = mutableListOf<BaseViewController>()
    /** スライド表示させた画面のビュー */
    private var nextScreenViews: List<View> = mutableListOf()
    /** スライド表示させる画面を追加する対象のビュー */
    open val nextScreenTargetView: View get() = throw AssertionError("When using the next screen, be sure to implement it in a subclass")
    /** スライドアニメーション時間 */
    var nextScreenAnimationDuration: Long = 300
    /** オーバーレイ画面のオーナー */
    var owner: BaseViewController? = null
    /** スライド表示させた画面の遷移のルート */
    var navigationRootController: BaseViewController? = null
    /** 画面表示中かどうか */
    var isForeground: Boolean = false
    /** 画面遷移アニメーション */
    var transitionAnimation: TransitionAnimation? = null


    /** オーバーレイの基準Z-index */
    private var overlayFloatingHeight: Float = 10.0f

    /**
     * 画面を追加する
     */
    fun added() {
        onAddedToScreen()
    }

    /**
     * 画面表示を開始する
     */
    fun enter() {
        foregroundController.onEnterForeground()
    }

    /**
     * 画面表示を終了する
     */
    fun leave() {
        foregroundController.onLeaveForeground()
    }

    /**
     * 画面を取り除く
     */
    fun removed() {
        onRemovedFromScreen()
        navigationRootController = null
    }

    /**
     * 画面がスクリーンに追加されたときの処理
     */
    open fun onAddedToScreen() {
        subControllers.forEach { it.added() }
    }

    /**
     * 画面がフォアグラウンド状態になったときの処理
     */
    open fun onEnterForeground() {
        isForeground = true
        enterForegroundSubControllers()
    }

    /**
     * 画面がフォアグラウンド状態から離脱したときの処理
     */
    open fun onLeaveForeground() {
        taskHolder.clearAll()
        leaveForegroundSubControllers()
        isForeground = false
    }

    /**
     * 画面がスクリーンから取り除かれたときの処理
     */
    open fun onRemovedFromScreen() {
        subControllers.forEach { it.removed() }
    }

    fun addSubController(controller: BaseViewController) {
        controller.owner = this
        subControllers.append(controller)
    }

    fun addSubControllers(controllers: List<BaseViewController>) {
        subControllers.forEach { it.owner = this }
        subControllers.append(contentsOf = controllers)
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        if (0 < nextScreens.count) {
            val translationX = -(view.width).toFloat()
            nextScreens.forEach {
                it.view.translationX = translationX
            }
            nextScreenTargetView.translationX = translationX
        }
        nextScreens.lastOrNull()?.view?.translationX = 0.0f
    }

    // region -> Next screen

    /**
     * "targetView"がrootViewに含まれているかをチェックする
     */
    private fun checkTargetView(targetView: View) {
        if (!targetView.isDescendant(view)) {
            throw AssertionError("The target view must be included in the view")
        }
    }

    /**
     * ViewControllerをスライド表示させる（targetView指定なし）
     */
    fun <T : BaseViewController> addNextScreen(type: KClass<T>, id: String? = null, cache: Boolean = true, animated: Boolean = true, prepare: ((T) -> Unit)? = null): T? {
        return addNextScreen(type, targetView = nextScreenTargetView, id = id, cache = cache, animated = animated, prepare = prepare)
    }

    /**
     * ViewControllerをスライド表示させる（targetView指定あり）
     */
    fun <T : BaseViewController> addNextScreen(type: KClass<T>, targetView: View, id: String? = null, cache: Boolean = true, animated: Boolean = true, prepare: ((T) -> Unit)? = null): T? {
        checkTargetView(targetView)
        val controller = ViewControllerCache.shared.get(type, id = id, cache = cache)
        val parentView = targetView.superview
        if (parentView == null || nextScreens.lastOrNull() == controller) {
            return null
        }
        controller.navigationRootController = this
        val prevView = nextScreens.lastOrNull()?.view ?: targetView

        leave()

        // 遷移アニメーション
        parentView.addView(controller.view)
        if (animated) {
            controller.view.isHidden = true
            val cancelListener = object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
            }
            android.os.Handler().postDelayed({
                nextScreens.add(controller)
                controller.view.animate().setDuration(0).translationX(controller.view.width.toFloat()).withEndAction {
                    controller.view.isHidden = false
                    controller.view.animate().setDuration(nextScreenAnimationDuration).translationX(0.0f).start()
                    prevView.animate().setDuration(nextScreenAnimationDuration).translationX(-parentView.width.toFloat()).withEndAction {
                    }.setListener(cancelListener).start()
                }.setListener(cancelListener)
            }, 0)
        } else {
            controller.view.translationX = 0.0f
            controller.view.isHidden = false
        }
        prepare?.invoke(controller)
        controller.added()
        controller.enter()
        return controller
    }

    /**
     * スライド表示させたViewControllerを１つ前に戻す（targetView指定なし）
     */
    fun removeNextScreen(animated: Boolean = true) {
        removeNextScreen(targetView = nextScreenTargetView, animated = animated)
    }

    /**
     * スライド表示させたViewControllerを１つ前に戻す（targetView指定あり）
     */
    fun removeNextScreen(targetView: View, animated: Boolean = true) {
        val removedScreen = this.nextScreens.removeLast() ?: return
        removedScreen.leave()
        enter()
        val completion = {
            removedScreen.view.removeFromSuperview()
            removedScreen.removed()
        }
        val prevView = nextScreens.lastOrNull()?.view ?: targetView
        if (animated) {
            removedScreen.view.animate().setDuration(nextScreenAnimationDuration).translationX(removedScreen.view.width.toFloat()).withStartAction {
                prevView.animate().setDuration(nextScreenAnimationDuration).translationX(0.0f).start()
            }.withEndAction {
                completion()
            }
        } else {
            prevView.translationX = 0.0f
            completion()
        }
    }

    /**
     * スライド表示させたViewControllerを全て閉じる
     */
    fun removeAllNextScreen(targetView: View? = null, executeStart: Boolean = false) {
        if (!isViewLoaded) {
            return
        }
        val targetView = targetView ?: nextScreenTargetView
        leave()
        nextScreens.forEach {
            it.view.removeFromSuperview()
            it.removed()
        }
        nextScreens.removeAll()
        targetView.translationX = 0.0f
        if (executeStart) {
            enter()
        }
    }

    // endregion

    // region -> Overlay

    /**
     * ViewControllerを上に乗せる
     */
    fun <T : BaseViewController> addOverlay(type: KClass<T>, id: String? = null, cache: Boolean = true, prepare: ((T) -> Unit)? = null): T? {
        val controller = ViewControllerCache.shared.get(type, id = id, cache = cache)
        controller.owner = this
        overlays.add(controller)
        view.addSubview(controller.view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            controller.view.z = overlayFloatingHeight * overlays.count
        }
        prepare?.invoke(controller)
        controller.added()
        controller.enter()
        return controller
    }

    /**
     * 上に乗ったViewControllerを外す
     */
    fun removeOverlay(target: KClass<out BaseViewController>? = null) {
        if (0 < overlays.size) {
            var removed: BaseViewController? = null
            val target = target
            if (target != null) {
                val index = overlays.indexOrNull { it::class == target }
                if (index != null) {
                    removed = overlays.removeAt(index)
                }
            } else {
                removed = overlays.removeLast()
            }
            removed?.owner = null
            removed?.view?.removeFromSuperview()
            removed?.leave()
            removed?.removed()
        }
    }

    /**
     * 上に乗ったViewControllerを全て外す
     */
    open fun removeAllOverlay() {
        val allOverlays = overlays.toMutableList()
        allOverlays.reversed().forEach {
            it.owner = null
            it.view.removeFromSuperview()
            it.leave()
            it.removed()
            this.overlays.remove(element = it)
        }
    }

    // endregion

    // region -> Other

    fun enterForegroundSubControllers() {
        if (isForeground) {
            foregroundSubControllers.forEach { it.enter() }
        }
    }

    fun leaveForegroundSubControllers() {
        if (isForeground) {
            foregroundSubControllers.forEach { it.leave() }
        }
    }

    // endregion

    // region -> Action

    /**
     * 1.Overlayが表示されている場合は１つ閉じる
     * 2.NextScreenが表示されている場合は１つ前に戻る
     * @return consumed 処理が実行されたか
     */
    open fun goBack(): Boolean {
        if (overlays.isNotEmpty()) {
            removeOverlay()
            return true
        } else if (nextScreens.isNotEmpty()) {
            removeNextScreen()
            return true
        }
        return false
    }

    // endregion
}
