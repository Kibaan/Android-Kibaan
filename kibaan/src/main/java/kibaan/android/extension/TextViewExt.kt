package kibaan.android.extension

import android.widget.TextView

var TextView.numberOfLines: Int
    get() {
        return if (maxLines == Integer.MAX_VALUE) {
            0
        } else {
            maxLines
        }
    }
    set(value) {
        maxLines = if (value == 0) {
            Integer.MAX_VALUE
        } else {
            value
        }
    }