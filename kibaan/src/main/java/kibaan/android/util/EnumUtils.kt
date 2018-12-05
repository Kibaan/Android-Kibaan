package kibaan.android.util

import kotlin.reflect.KClass

/**
 *
 * Created by yamamoto on 2018/01/25.
 */
object EnumUtils {

    fun <T: Enum<*>>getEnumValue(type: KClass<T>, name: String?) : T? {
        return try {
            @Suppress("UPPER_BOUND_VIOLATED", "UNCHECKED_CAST")
            val result = java.lang.Enum.valueOf<Enum<*>>(type.java as Class<Enum<*>>, name)
            @Suppress("UNCHECKED_CAST")
            result as? T
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }
}