package kibaan.android.framework

import kibaan.android.ios.removeAll
import kibaan.android.ios.removeFromSuperview
import java.lang.IllegalStateException
import kotlin.reflect.KClass

/**
 * ViewControllerをCacheする
 * Created by Yamamoto Keita on 2018/01/18.
 */
class ViewControllerCache {
    companion object {
        val shared: ViewControllerCache
            get() = SingletonContainer.shared.get(ViewControllerCache::class)
    }

    private var controllerMap: MutableMap<String, SmartViewController> = mutableMapOf()

    fun <T : SmartViewController> get(type: KClass<T>, layoutName: String? = null, id: String? = null, cache: Boolean = true): T {
        var controllerKey = type.java.name
        val suffix = if (id != null) ".$id" else ""
        controllerKey += suffix

        var controller = if (cache) controllerMap[controllerKey] else null
        if (controller == null) {
            controller =
                    create(type = type, layoutName = layoutName, id = id)
            controllerMap[controllerKey] = controller
        }

        @Suppress("UNCHECKED_CAST")
        return controller as T
    }

    fun <T : SmartViewController> create(type: KClass<T>, layoutName: String? = null, id: String? = null): T {
        val controller: T = if (layoutName != null) {
            try {
                type.java.getConstructor(String::class.java).newInstance(layoutName)
            } catch (e: NoSuchMethodException) {
                throw IllegalStateException("If you set layout name `$layoutName`, ${type.simpleName} class must have constructor(String).", e)
            }
        } else {
            type.java.newInstance()
        }
        val baseViewController = controller as? SmartViewController
        if (baseViewController != null && id != null) {
            baseViewController.viewID = id
        }
        return controller
    }

    fun <T : SmartViewController> getCache(type: KClass<T>, id: String? = null): T? {
        var controllerKey = type.java.name
        val suffix = if (id != null) ".$id" else ""
        controllerKey += suffix

        @Suppress("UNCHECKED_CAST")
        return controllerMap[controllerKey] as? T
    }

    fun clear(completion: (() -> Unit)? = null) {
        controllerMap.values.forEach {
            if (it.isViewLoaded) {
                it.view.removeFromSuperview()
            }
        }
        controllerMap.removeAll()
        completion?.invoke()
    }
}
