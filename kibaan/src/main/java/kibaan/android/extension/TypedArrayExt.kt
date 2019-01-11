package kibaan.android.extension

import android.content.res.TypedArray

/**
 * 引数に指定した属性が存在する場合はStringとして取得し、存在しな場合はnullを返す.
 */
fun TypedArray.getStringOrNull(id: Int): String? {
    return if (hasValue(id)) getString(id) else null
}