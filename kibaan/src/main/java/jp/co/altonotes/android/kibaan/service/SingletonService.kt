package jp.co.altonotes.android.kibaan.service

import jp.co.altonotes.android.kibaan.ios.removeAll
import kotlin.reflect.KClass

/**
 * シングルトンなインスタンスを管理するサービス
 */
object SingletonService {

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