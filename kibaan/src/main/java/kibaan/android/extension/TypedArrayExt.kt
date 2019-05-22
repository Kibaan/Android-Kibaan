package kibaan.android.extension

import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import kibaan.android.ios.UIColor

/**
 * 引数に指定した属性が存在する場合はStringとして取得し、存在しな場合はnullを返す.
 */
fun TypedArray.getStringOrNull(id: Int): String? {
    return if (hasValue(id)) getString(id) else null
}

/**
 * 引数にしていた属性が存在する場合はUIColorとして取得し、存在しない場合はnullを返す.
 */
fun TypedArray.getUIColorOrNull(id: Int): UIColor? {
    return if (hasValue(id)) UIColor(getColor(id, Color.BLACK)) else null
}

/**
 * 引数にしていた属性が存在する場合はBitmapとして取得し、存在しない場合はnullを返す.
 */
fun TypedArray.getBitmapOrNull(id: Int): Bitmap? {
    return if (hasValue(id)) {
        BitmapFactory.decodeResource(resources, getResourceId(id, -1))
    } else {
        null
    }
}