package kibaan.android.ios

import kotlin.reflect.KProperty

/**
 * DidSet
 * Created by yamamoto on 2018/04/26.
 */
class DidSet<T>(private var value: T) {

    private var didSetProcess: (()->Unit)? = null
    private var didSetProcessWithOldValue: ((T)->Unit)? = null

    constructor(value: T, didSetProcess: ()->Unit): this(value) {
        this.didSetProcess = didSetProcess
    }

    constructor(value: T, didSetProcessWithOldValue: (T)->Unit): this(value) {
        this.didSetProcessWithOldValue = didSetProcessWithOldValue
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val oldValue = this.value
        this.value = value
        didSetProcess?.invoke()
        didSetProcessWithOldValue?.invoke(oldValue)
    }
}

fun <T> didSet(initial: T, didSetProcess: ()->Unit): DidSet<T> {
    return DidSet(value = initial, didSetProcess = didSetProcess)
}

fun <T> didSetWithOld(initial: T, didSetProcess: (T)->Unit): DidSet<T> {
    return DidSet(value = initial, didSetProcessWithOldValue = didSetProcess)
}