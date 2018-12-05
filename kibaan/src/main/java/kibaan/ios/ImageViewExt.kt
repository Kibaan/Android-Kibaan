package kibaan.ios

import android.graphics.drawable.Drawable
import android.widget.ImageView

var ImageView.image: Drawable?
    get() = drawable
    set(value) = setImageDrawable(value)