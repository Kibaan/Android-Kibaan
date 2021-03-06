package kibaan.android.extension

import org.json.JSONException
import org.json.JSONObject

fun JSONObject.getStringOrNull(name: String): String? {
    return if (isNull(name)) {
        null
    } else {
        try {
            getString(name)
        } catch (e: JSONException) {
            null
        }
    }
}

fun JSONObject.getIntOrNull(name: String): Int? {
    return if (isNull(name)) {
        null
    } else {
        try {
            val jsonObject = get(name)
            val str = jsonObject as? String?
            return if (str?.contains(".").isTrue) {
                null
            } else {
                return getInt(name)
            }
        } catch (e: JSONException) {
            null
        }
    }
}