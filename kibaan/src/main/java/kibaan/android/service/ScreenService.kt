package kibaan.android.service

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import kibaan.android.AndroidUnique
import kibaan.android.controller.BaseViewController
import kibaan.android.controller.SmartActivity
import kibaan.android.controller.ViewControllerCache
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
        val shared: ScreenService get() = SingletonService.get(ScreenService::class)

        fun setActivity(activity: SmartActivity?) {
            ScreenService.activity = activity
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

    private var screenStack: MutableList<BaseViewController> = mutableListOf()
    private var screenIndicator: ScreenIndicator? = null
    val foregroundController: BaseViewController? get() = screenStack.lastOrNull()

    @AndroidUnique
    private var rootViewController: UIViewController? = null
        set(value) {
            if (field != value) {
                val lastController = field
                if (lastController is BaseViewController) {
                    lastController.leave()
                }

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
        val controller = ViewControllerCache.get(type)
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

    fun <T : BaseViewController> addSubScreen(type: KClass<T>, nibName: String? = null, id: String? = null, cache: Boolean = true, prepare: ((T) -> Unit)? = null): T? {
        foregroundController?.leave()
        val controller = ViewControllerCache.get(type, layoutName = nibName, id = id, cache = cache)
        screenStack.add(controller)
        activity?.rootContainer?.addSubview(controller.view)
        prepare?.invoke(controller)
        controller.added()
        controller.enter()
        return controller
    }

    fun removeSubScreen(executeStart: Boolean = true, targetType: KClass<out BaseViewController>? = null) {
        foregroundController?.leave()
        while (true) {
            var removed: BaseViewController? = null
            if (1 < screenStack.size) {
                removed = screenStack.removeLast()
                removed?.view?.removeFromSuperview()
                removed?.removed()
            }
            if (removed == null || targetType == null || removed::class == targetType) {
                break
            }
        }
        if (executeStart) {
            screenStack.lastOrNull()?.enter()
        }
    }

    fun removeAllSubScreen() {
        foregroundController?.leave()
        foregroundController?.foregroundController?.removeOverlay()
        while (1 < screenStack.size) {
            val removed = screenStack.removeLast()
            removed?.removeOverlay()
            removed?.view?.removeFromSuperview()
            removed?.removed()
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
