package kibaan.android.framework

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import kibaan.android.AndroidUnique
import kibaan.android.extension.isTrue
import kibaan.android.ios.*
import kibaan.android.ui.ScreenIndicator
import kotlin.reflect.KClass

/**
 * 画面遷移や共通的な画面表示機能
 */
@SuppressLint("StaticFieldLeak")
class ScreenService {

    companion object {
        private var activity: SmartActivity? = null
        val shared: ScreenService
            get() = SingletonContainer.shared.get(ScreenService::class)

        fun setActivity(activity: SmartActivity?) {
            Companion.activity = activity
            if (activity != null) {
                shared.init(context = activity)
            }
        }
    }

    val context: Context? get() = activity

    var windowColor: UIColor?
        get() {
            return activity?.rootContainer?.backgroundColor
        }
        set(value) {
            activity?.rootContainer?.backgroundColor = value
        }

    var defaultTransitionAnimation: TransitionAnimation? = TransitionAnimation.coverVertical

    private var screenStack: MutableList<BaseViewController> = mutableListOf()
    private var screenIndicator: ScreenIndicator? = null
    val foregroundController: BaseViewController? get() = screenStack.lastOrNull()

    @AndroidUnique
    private var rootViewController: UIViewController? = null
        set(value) {
            if (field != value) {

                field = value

                val container = activity?.rootContainer
                container?.removeAllViews()

                if (value != null) {
                    container?.addView(value.view)
                }
            }
        }

    init {
    }

    fun init(context: Context) {
        screenIndicator = ScreenIndicator(context)
    }

    fun <T : BaseViewController> setRoot(type: KClass<T>, prepare: ((T) -> Unit)? = null): T {
        val controller = ViewControllerCache.shared.get(type)
        if (rootViewController == controller) {
            return controller
        }
        screenStack.forEach {
            it.view.removeFromSuperview()
            it.removed()
        }
        screenStack.removeAll()
        setRootViewController(controller, prepare = prepare)
        screenStack.add(controller)
        return controller
    }

    fun <T : BaseViewController> setRootViewController(viewController: T, prepare: ((T) -> Unit)? = null) {
        val oldRootViewController = rootViewController as? BaseViewController
        oldRootViewController?.leave()
        rootViewController = viewController
        oldRootViewController?.removed()
        prepare?.invoke(viewController)
        viewController.added()
        viewController.enter()
    }

    fun <T : BaseViewController> addSubScreen(type: KClass<T>,
                                              nibName: String? = null,
                                              id: String? = null,
                                              cache: Boolean = true,
                                              transitionType: TransitionType = TransitionType.normal,
                                              prepare: ((T) -> Unit)? = null): T? {
        activity?.isUserInteractionEnabled = false

        foregroundController?.leave()
        val controller = ViewControllerCache.shared.get(type, layoutName = nibName, id = id, cache = cache)
        screenStack.add(controller)
        activity?.rootContainer?.addSubview(controller.view)

        val finish: () -> Unit = {
            activity?.isUserInteractionEnabled = true
        }
        val isNormal = transitionType == TransitionType.normal
        controller.transitionAnimation = if (isNormal) defaultTransitionAnimation else transitionType.animation
        if (controller.transitionAnimation != null) {
            controller.transitionAnimation?.animator?.animateIn(controller.view, completion = finish)
        } else {
            finish.invoke()
        }
        prepare?.invoke(controller)
        controller.added()
        controller.enter()
        return controller
    }


    fun removeSubScreen(executeStart: Boolean = true, completion: (() -> Unit)? = null) {
        if (screenStack.size <= 1) {
            return
        }
        activity?.isUserInteractionEnabled = false
        foregroundController?.leave()
        val removed = screenStack.removeLast() ?: return
        val finish: () -> Unit = {
            removed.view.removeFromSuperview()
            removed.removed()
            completion?.invoke()
            activity?.isUserInteractionEnabled = true
        }
        if (removed.transitionAnimation != null) {
            removed.transitionAnimation?.animator?.animateOut(removed.view, completion = finish)
        } else {
            finish()
        }
        if (executeStart) {
            screenStack.lastOrNull()?.enter()
        }
    }

    fun removeSubScreen(executeStart: Boolean = true, to: BaseViewController, completion: (() -> Unit)? = null) {
        if (screenStack.size <= 1 || !screenStack.contains(to)) {
            return
        }
        val lastViewController = screenStack.last()
        if (to === lastViewController) {
            return
        }
        activity?.isUserInteractionEnabled = false
        foregroundController?.leave()

        val removedViewControllers: MutableList<BaseViewController?> = mutableListOf()
        for (viewController in screenStack.reversed()) {
            if (screenStack.lastOrNull() === to) {
                break
            }
            val removed = screenStack.removeLast()
            removedViewControllers.append(removed)
            if (viewController != lastViewController) {
                viewController.view.removeFromSuperview()
            }
        }
        val finish: () -> Unit = {
            lastViewController.view.removeFromSuperview()
            removedViewControllers.forEach {
                it?.removed()
            }
            completion?.invoke()
            activity?.isUserInteractionEnabled = true
        }
        if (lastViewController.transitionAnimation != null) {
            lastViewController.transitionAnimation?.animator?.animateOut(lastViewController.view, completion = finish)
        } else {
            finish()
        }
        if (executeStart) {
            screenStack.lastOrNull()?.enter()
        }
    }

    fun removeAllSubScreen(completion: (() -> Unit)? = null) {
        if (screenStack.size <= 1) {
            return
        }
        activity?.isUserInteractionEnabled = false
        foregroundController?.leave()
        foregroundController?.foregroundController?.removeAllOverlay()

        val lastViewController = screenStack.last()
        val removedViewControllers: MutableList<BaseViewController?> = mutableListOf()
        while (1 < screenStack.size) {
            val removed = screenStack.removeLast()
            removedViewControllers.append(removed)
            if (removed != lastViewController) {
                removed?.view?.removeFromSuperview()
            }
        }
        val finish: () -> Unit = {
            lastViewController.view.removeFromSuperview()
            removedViewControllers.forEach {
                it?.removed()
            }
            completion?.invoke()
            activity?.isUserInteractionEnabled = true
        }
        if (lastViewController.transitionAnimation != null) {
            lastViewController.transitionAnimation?.animator?.animateOut(lastViewController.view, completion = finish)
        } else {
            finish()
        }
        screenStack.firstOrNull()?.enter()
    }

    /**
     * 画面全体にインジケーターを表示する
     */
    fun showScreenIndicator(): View? {
        val indicator = screenIndicator ?: return null
        indicator.visibility = View.VISIBLE
        activity?.rootContainer?.addSubview(indicator)
        return indicator
    }

    /**
     * １つ前の画面に戻る
     * @return consumed 処理を実行したか
     */
    @AndroidUnique
    fun goBack(): Boolean {
        if (foregroundController?.goBack().isTrue) {
            return true
        }
        if (1 < screenStack.count) {
            removeSubScreen()
            return true
        }
        return false
    }

}
