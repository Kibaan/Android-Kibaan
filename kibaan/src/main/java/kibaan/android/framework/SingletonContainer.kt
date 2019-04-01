package kibaan.android.framework

import kibaan.android.ios.removeAll
import kotlin.reflect.KClass

/**
 * シングルトンのインスタンスを保持する
 */
class SingletonContainer {

    companion object {
        var shared = SingletonContainer()
    }

    var map: MutableMap<KClass<*>, Any> = mutableMapOf()

    fun <T : Any> get(type: KClass<T>): T {
        var instance = map[type] as? T
        if (instance != null) {
            return instance
        }
        instance = type.java.newInstance()
        map[type] = instance
        return instance
    }

    fun remove(type: KClass<*>) {
        map.remove(type)
    }

    fun clear() {
        map.removeAll()
    }
}