package kibaan.android.ios

import kotlin.reflect.KClass

/**
 * Enum for String
 */
interface StringEnum {
    val rawValue: String
}

interface StringEnumDefault : StringEnum {
    val name: String
    override val rawValue: String
        get() = name
}

@Suppress("unused")
inline fun <reified T> KClass<T>.value(rawValue: String?): T? where T : Enum<T>, T : StringEnum {
    return enumValues<T>().firstOrNull { it.rawValue == rawValue }
}

/**
 * Enum for Int
 */
interface IntEnum {
    val rawValue: Int
}

interface IntEnumDefault : IntEnum {
    val ordinal: Int
    override val rawValue: Int
        get() = ordinal
}

@Suppress("unused")
inline fun <reified T> KClass<T>.value(rawValue: Int?): T? where T : Enum<T>, T : IntEnum {
    return enumValues<T>().firstOrNull { it.rawValue == rawValue }
}

