package kibaan.extension

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
            val str = getString(name)
            return if (str.contains(".")) {
                null
            } else {
                return getInt(name)
            }
        } catch (e: JSONException) {
            null
        }
    }
}