package jp.co.altonotes.android.kibaan.controller

import jp.co.altonotes.android.kibaan.ios.removeAll
import jp.co.altonotes.android.kibaan.ios.removeFromSuperview
import kotlin.reflect.KClass

/**
 * ViewControllerをCacheする
 * Created by Yamamoto Keita on 2018/01/18.
 */
object ViewControllerCache {
    private var controllerMap: MutableMap<String, BaseViewController> = mutableMapOf()

    fun <T : BaseViewController> get(type: KClass<T>, layoutName: String? = null, id: String? = null, cache: Boolean = true): T {
        var controllerKey = type.java.name
        val suffix = if (id != null) ".$id" else ""
        controllerKey += suffix

        var controller = if (cache) controllerMap[controllerKey] else null
        if (controller == null) {
            controller = create(type = type, layoutName = layoutName, id = id)
            controllerMap[controllerKey] = controller
        }

        @Suppress("UNCHECKED_CAST")
        return controller as T
    }

    fun <T : BaseViewController> create(type: KClass<T>, layoutName: String? = null, id: String? = null): T {
        val controller: T = if (layoutName != null) {
            type.java.getConstructor(String::class.java).newInstance(layoutName)
        } else {
            type.java.newInstance()
        }
        val baseViewController = controller as? BaseViewController
        if (baseViewController != null && id != null) {
            baseViewController.viewID = id
        }
        return controller
    }

    fun <T : BaseViewController> getCache(type: KClass<T>, id: String? = null): T? {
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
