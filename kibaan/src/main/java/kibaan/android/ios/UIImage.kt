package kibaan.android.ios

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import kibaan.android.extension.cgFloatValue

class UIImage {

    var drawable: Drawable? = null

    val size: CGSize
        get() {
            val drawable = drawable ?: return CGSize.zero
            return CGSize(drawable.intrinsicWidth.cgFloatValue, drawable.intrinsicHeight.cgFloatValue)
        }

    constructor(context: Context?, named: String, defType: String = "drawable") {
        if (context == null) return 
        val id = context.resources.getIdentifier(named, defType, context.packageName)
        if (id != 0) {
            drawable = ContextCompat.getDrawable(context, id)
        }
    }

    constructor(filePath: String) {
        // FIXME
        throw NotImplementedError()
    }

    constructor(data: Data) {
        // FIXME
        throw NotImplementedError()
    }

    fun jpegData(compressionQuality: CGFloat): Data? {
        // FIXME
        throw NotImplementedError()
    }

    fun pngData(): Data? {
        // FIXME
        throw NotImplementedError()
    }
}